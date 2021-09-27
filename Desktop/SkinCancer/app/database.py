import pandas as pd

def save(result):
    idx = len(pd.read_csv("database.csv"))
    new_df = pd.DataFrame({"result":result}, 
                         index = [idx])
    new_df.to_csv("database.csv",mode = "a", header = False)
    return None

def load_list():
    dot_list = []
    df = pd.read_csv("database.csv")
    for i in range(len(df)):
        dot_list.append(df.iloc[i].tolist())
    print(dot_list)
    # return dot_list

def now_index():
    df = pd.read_csv("database.csv")
    return len(df)-1


def load_dot(idx):
    df = pd.read_csv("database.csv")
    dot_info = df.iloc[idx]
    return dot_info


if __name__ =="__main__":
    load_list()