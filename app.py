import torch
from flask import Flask, jsonify
from flask import request
import io
import torchvision.transforms as transforms
from PIL import Image
import json
import os
import requests



def transform_image(image_bytes):
    trans = transforms.Compose(
        [transforms.Resize((150, 150)), transforms.ToTensor(),
         transforms.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))])
    image = Image.open(io.BytesIO(image_bytes))
    return trans(image).unsqueeze(0)

model= torch.load('C:/Users/sobin/PycharmProjects/pythonProject1/model_test4.pt')

model.eval()

imagenet_class_index = json.load(open('C:/Users/sobin/PycharmProjects/pythonProject1/index.json'))

#if os.path.isfile(imagenet_class_index):
    #with open (imagenet_class_index) as f:
        #img_class_map = json.load(f)

def get_prediction(image_bytes):
    tensor = transform_image(image_bytes=image_bytes)
    outputs = model.forward(tensor)
    _, y_hat = outputs.max(1)
    predicted_idx = str(y_hat.item())
    #prediction = y_hat.item()
    return imagenet_class_index[predicted_idx]#str(prediction)

#def render_prediction(prediction_idx):
    #stridx = str(prediction_idx)
    #class_name = 'Unknown'
    #if img_class_map is not None:
        #if stridx in img_class_map is not None:
            #class_name = img_class_map[stridx][1]
    #return prediction_idx, class_name


with open("C:/Users/sobin/PycharmProjects/pythonProject1/ISIC_0010909.jpg", 'rb') as f:
    image_bytes = f.read()
    print(get_prediction(image_bytes=image_bytes))



app = Flask(__name__)

@app.route('/', methods=['GET'])
def root():
    return jsonify({'msg' : 'Try POSTing to the /predict endpoint with an RGB image attachment'})

@app.route('/predict', methods=['POST'])
def predict():
    if request.method == 'POST':
        #data=request.json

        #Request로부터 파일 받기
        file = request.files['file']
        #if file is not None:
            #input_tensor = transform_image(file)
            #prediction_idx = get_prediction(input_tensor)
            #class_ID, class_name = render_prediction(prediction_idx)
            #return jsonify({'class_ID': class_ID, 'class_name': class_name})
        # 파일을 바이트로
        img_bytes = file.read()
        #예측해서 반환
        class_name = get_prediction(image_bytes=img_bytes)
        return jsonify({'class_name': class_name})




if __name__ == '__main__':
    app.run()

