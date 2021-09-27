from flask import Flask, render_template, request, redirect, url_for
import sys
import database
application = Flask(__name__)


@application.route("/")
def main():
    return render_template("main.html")

@application.route("/photo")
def photo():
    result = request.args.get("result")
    if result == None:
        result = False
    else:
        result = True
    
    database.save(result)
    return render_template("photo.html")

@application.route("/upload_done", methods=["POST"])
def upload_done():
    uploaded_files = request.files["file"]
    uploaded_files.save("static/img/{}.jpeg".format(database.now_index()))
    
    return redirect(url_for("main"))

@application.route("/mylist")
def mylist():
    dot_list = database.load_list()
    length = len(dot_list)
    return render_template("mylist.html", dot_list = dot_list, length = length)

@application.route("/dot_info/<int:index>/")
def dot_info(index):
    dot_info = database.load_dot(index)
    result = dot_info["result"]
    photo = f"img/{index}.jpeg"
    
    return render_template("dot_info.html", result = result, photo = photo)



if __name__ == "__main__":
    # 서버 실행
    application.run(host='0.0.0.0', debug=True, port=80)
