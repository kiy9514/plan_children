import torch
import torch.nn as nn
import torch.optim as optim
from torch.optim import lr_scheduler
import numpy as np
import torchvision
from torchvision import datasets, models, transforms
import matplotlib.pyplot as plt
import time
import os
import copy
import cv2
import albumentations
import albumentations.pytorch
from PIL import Image
import random
from numpy.testing._private.parameterized import param

#trans = transforms.Compose(
    #[transforms.Resize((150,150)),transforms.ToTensor(),
     #transforms.Normalize((0.5,0.5,0.5),(0.5,0.5,0.5)),
     #transforms.ColorJitter(brightness=0.2, contrast=0.2, saturation=0.2, hue=0.2),
     #])

trans = transforms.Compose([
    transforms.Resize((150, 150)),
    transforms.ToTensor(),
    transforms.Normalize((0.5,0.5,0.5),(0.5,0.5,0.5)),
    transforms.ColorJitter(brightness=0.2, contrast=0.2, saturation=0.2, hue=0.2)])
    #transforms.RandomHorizontalFlip(p = 1)])
trans_test = transforms.Compose([
    transforms.Resize((150,150)),
    transforms.ToTensor(),
    transforms.Normalize((0.5,0.5,0.5),(0.5,0.5,0.5))])
#albumentations_transform_oneof = albumentations.Compose([
    #albumentations.Resize(300, 300),
    #albumentations.RandomCrop(224, 224),
    #albumentations.OneOf([
    #                      albumentations.HorizontalFlip(p=1),
    #                      albumentations.RandomRotate90(p=1),
    #                      albumentations.VerticalFlip(p=1)
    #], p=1),
    #albumentations.OneOf([
    #                      albumentations.MotionBlur(p=1),
    #                      albumentations.OpticalDistortion(p=1),
    #                      albumentations.GaussNoise(p=1)
    #], p=1),
    #albumentations.pytorch.ToTensor()
#])

#albumentation_dataset_oneof = AlbumentationsDataset(
    #path = 'images/',
    #transform = albumentations_transform_oneof
#)

#num_samples = 10
#fig, ax = plt.subplots(1, num_samples, figsize=(25, 5))
#for i in range(num_samples):
#  ax[i].imshow(transforms.ToPILImage()(albumentation_dataset_oneof[0][0]))
#  ax[i].axis('off')

#trainset = torchvision.datasets.ImageFolder(root="./datasets/train", transform=trans)
#trainloader = torch.utils.data.DataLoader(trainset, batch_size=20, shuffle=True, num_workers=0)

#valset = torchvision.datasets.ImageFolder(root="./datasets/evaluation", transform=trans)
#validloader = torch.utils.data.DataLoader(trainset, batch_size=20, shuffle=True, num_workers=0)

testset = torchvision.datasets.ImageFolder(root="./datasets/test", transform=trans_test)
testloader = torch.utils.data.DataLoader(testset, batch_size=4, shuffle=True, num_workers=0)



image_datasets = {'train': datasets.ImageFolder(root="./datasets/train", transform=trans),
                  'val': datasets.ImageFolder(root="./datasets/evaluation", transform=trans_test)}

dataloaders = { 'train' : torch.utils.data.DataLoader(image_datasets['train'], batch_size=4,
                                             shuffle=True, num_workers=0),
                'val' : torch.utils.data.DataLoader(image_datasets['val'], batch_size=4,
                                             shuffle=True, num_workers=0) }

dataset_sizes = {x: len(image_datasets[x]) for x in ['train', 'val']}

class_names = image_datasets['train'].classes


device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
print(device)


def imshow(inp, title=None):
    """Imshow for Tensor."""
    inp = inp.numpy().transpose((1, 2, 0))
    mean = np.array([0.485, 0.456, 0.406])
    std = np.array([0.229, 0.224, 0.225])
    inp = std * inp + mean   # 정규화를 해제
    inp = np.clip(inp, 0, 1)
    plt.imshow(inp)
    if title is not None:
        plt.title(title)
    plt.pause(5)

#class_names = trainset.classes


# 한개의 배치(batch)만큼 이미지를 불러온다. 배치 사이즈를 4로 했으니 사진 4장이 로드된다.
#inputs, classes = next(iter(dataloaders['train']))

# 로드된 데이터에 make_grid 함수를 통해 그리드를 추가한다.
#out = torchvision.utils.make_grid(inputs)

# 이미지를 출력한다.
#imshow(out, title=[class_names[x] for x in classes])

#google inception model
#model_ft = models.inception_v3(pretrained=True)
#FC_in_features = model_ft.fc.in_features
#model_ft.fc=nn.Linear(in_features=FC_in_features, out_features=3)

#resnet
#model_ft = models.resnet152(pretrained=True)
#num_ftrs = model_ft.fc.in_features
#model_ft.fc = nn.Linear(num_ftrs, 3)

#vgg
model_ft = models.vgg19(pretrained=True)
num_ftrs = model_ft.features
for params in num_ftrs.parameters():
    param.requires_grad = False
model_ft.classifier[3].out_features = 3

model_ft = model_ft.to(device)

criterion = nn.CrossEntropyLoss()

# Observe that all parameters are being optimized
optimizer_ft = optim.SGD(model_ft.parameters(), lr=0.001, momentum=0.9)

# Decay LR by a factor of 0.1 every 7 epochs
exp_lr_scheduler = lr_scheduler.StepLR(optimizer_ft, step_size=7, gamma=0.1)

# 모델 훈련
def train_model(model, criterion, optimizer, scheduler, num_epochs=5):
    since = time.time() #시작 시간을 기록(총 소요 시간 계산을 위해)

    best_model_wts = copy.deepcopy(model.state_dict())
    best_acc = 0.0

    for epoch in range(num_epochs):
        print('Epoch {}/{}'.format(epoch, num_epochs - 1)) #epoch를 카운트
        print('-' * 10)

        # Each epoch has a training and validation phase
        for phase in ['train', 'val']:  #train mode와 validation mode 순으로 진행
            if phase == 'train':
                scheduler.step()
                model.train()  # Set model to training mode
            else:
                model.eval()   # Set model to evaluate mode

            running_loss = 0.0
            running_corrects = 0

            # Iterate over data.
            for inputs, labels in dataloaders[phase]: #dataloader로부터 dataset과 그에 해당되는 label을 불러옴
                inputs = inputs.to(device) #GPU로 입력데이터를 올림
                labels = labels.to(device) #GPU로 label을 올림

                # zero the parameter gradients
                optimizer.zero_grad() #Gradient를 0으로 초기화

                # forward
                # track history if only in train
                with torch.set_grad_enabled(phase == 'train'):
                    outputs = model(inputs)
                    _, preds = torch.max(outputs, 1) #마지막 layer에서 가장 값이 큰 1개의 class를 예측 값으로 지정
                    loss = criterion(outputs, labels)

                    # backward + optimize only if in training phase
                    if phase == 'train': # training 모드에서는 weight를 update한다.
                        loss.backward() #backward
                        optimizer.step()

                # statistics
                running_loss += loss.item() * inputs.size(0)
                running_corrects += torch.sum(preds == labels.data)

            epoch_loss = running_loss / dataset_sizes[phase]
            epoch_acc = running_corrects.double() / dataset_sizes[phase]

            print('{} Loss: {:.4f} Acc: {:.4f}'.format(
                phase, epoch_loss, epoch_acc))

            # deep copy the model
            if phase == 'val' and epoch_acc > best_acc:
                best_acc = epoch_acc
                best_model_wts = copy.deepcopy(model.state_dict())

        print()

    time_elapsed = time.time() - since
    print('Training complete in {:.0f}m {:.0f}s'.format(
        time_elapsed // 60, time_elapsed % 60))
    print('Best val Acc: {:4f}'.format(best_acc))

    # load best model weights
    model.load_state_dict(best_model_wts)
    return model

model_ft = train_model(model_ft, criterion, optimizer_ft, exp_lr_scheduler, num_epochs=5)

#PATH_1='./datasets'

torch.save(model_ft,'model_test4.pt')
#torch.save(model_ft.state_dict(),'model_test_dict2.pt')
#torch.save({
    #'model': model_ft.state_dict(),
    #'optimizer': optimizer_ft.state_dict()
#}, 'all2.tar')

# testdata 정확도 알아보기
correct = 0
total = 0
with torch.no_grad():
    for data in testloader:
        images, labels = data
        outputs = model_ft(images)
        _, predicted = torch.max(outputs.data, 1)
        total += labels.size(0)
        correct += (predicted == labels).sum().item()

print('Accuracy of the network on the test images: %d %%' % (
    100 * correct / total))
