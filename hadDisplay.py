from guizero import *
from datetime import datetime, time
#from tkinter import Label, PhotoImage
#from PIL import Image, ImageTk
import pyrebase
import socket
import requests
import json
import time
import subprocess

# Initial Setup Region

# There is a glitch with the pyrebase library when dealing with quotes
# This will temporarily replace quote function to only add quotes once to API calls
def noquote(s):
    return s
pyrebase.pyrebase.quote = noquote

print(system_config.supported_image_types)

subprocess.call(['./checkWiFi.sh'])

# Get current device IP address
testIP = "8.8.8.8"
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
ipaddr = sock.getsockname()[0]
host = socket.gethostname()

# API Call to ipstack.com to get public IP Address
getLoc = "http://api.ipstack.com/check?access_key=26f340ead375064984cfaccafb9aea87"
locReq = requests.get(getLoc)

if locReq.status_code != 200:
    print(resp.status_code)

locData = json.loads(locReq.text)

ipAddr = locData['ip']

print(ipAddr)

country_code = locData['country_code']

units = ""

if country_code == "US":
    units = "imperial"
else:
    units = "metric"

lat = locData['latitude']
long = locData['longitude']

foreLink = "http://api.openweathermap.org/data/2.5/forecast?lat="+str(lat)+"&lon="+str(long)+"&units="+units+"&cnt=10&appid=39177e03ec521ba7e70353e7e3d960ee"
print(foreLink)
foreReq = requests.get(foreLink)
foreData = json.loads(foreReq.text)

weatLink = "http://api.openweathermap.org/data/2.5/weather?lat="+str(lat)+"&lon="+str(long)+"&units="+units+"&appid=39177e03ec521ba7e70353e7e3d960ee"
print(weatLink)
weatReq = requests.get(weatLink)
weatData = json.loads(weatReq.text)

def updateTime():
    currentDtTxt.clear()
    curDt = datetime.now()
    currentDtTxt.append("Welcome " + str(checkName()) + "!\n\n It is: " + curDt.strftime("%m-%d-%Y %I:%M %p"))
    currentDtTxt.after(1000, updateTime)

def privacyMask():
    privacy = db.child("Privacy Mode").child("Mode").get()
    todoList = db.child("ToDo List").child("Item").get()
    todoTxt.clear()
    try:
        for t in todoList.each():
            if privacy.val() == "Privacy Off":
                todoName = t.val()
                todoTxt.append(todoName + "\n")
            else:
                todoTxt.append("Something to do\n")
    except TypeError:
        print("There are no items!\n")
    if todoBox.visible == True:
        todoTxt.after(2000, privacyMask)
        

def privacyCalMask():
    privacy = db.child("Privacy Mode").child("Mode").get()
    calList = db.child("tasks").order_by_child("dayname").get()
    calTxt.clear()
    try:
        for t in calList.each():
            calKey = t.key()
            calItem = db.child("tasks").child(calKey).get().val()
            if privacy.val() == "Privacy Off":
                calTxt.append(calItem['dayname'] + "\n")
            else:
                calTxt.append("Something to do\n")
    except TypeError:
        print("There are no items!\n")
    if calBox.visible == True:
        calTxt.after(2000, privacyCalMask)

def weatherInfo():
    global weatData
    global wthrPic
    global curDt
    isNight = False
    curTemp = weatData['main']['temp']
    curMaxTmp = weatData['main']['temp_max']
    curMinTmp = weatData['main']['temp_min']
    curCondType = weatData['weather'][0]['main']
    curCond = weatData['weather'][0]['description']
    if curDt.hour >= 21 or curDt.hour <= 6:
        isNight = True
    else:
        isNight = False
    wthrTxt.clear()
    wthrTxt.append("  " + curCond + "\n\n")
    wthrTxt.append("  Temp:" + str(round(curTemp, 0)) + "\n\n")
    wthrTxt.append("  High: " + str(round(curMaxTmp, 0)) + "\n\n")
    wthrTxt.append("  Low: " + str(round(curMinTmp, 0)) + "\n\n")
    

def checkName():
    firstName = ""
    ui = db.child("User Info").child("First Name").get()
    if ui.val() is None:
        firstName = "user"
    else:
        firstName = ui.val()
    return firstName

def switchView():
    if todoBox.visible == True:
        todoBox.visible = False
        calBox.visible = True
        wthrBox.visible = False
        privacyCalMask()
        print("Calendar Accessed")
        calBox.after(10000, switchView)
    elif calBox.visible == True:
        todoBox.visible = False
        calBox.visible = False
        wthrBox.visible = True
        weatherInfo()
        print("Weather Accessed")
        wthrBox.after(10000, switchView)
    else:
        todoBox.visible = True
        calBox.visible = False
        wthrBox.visible = False
        privacyMask()
        print("To-Do Accessed")
        todoBox.after(10000, switchView)

#def testView():
#    global imgStr
#    if imgStr == "none" or imgStr == "weather/brokenclouds-day.jpg":
#        imgStr = "weather/brokenclouds-night.jpg"
#    else:
#        imgStr = "weather/brokenclouds-day.jpg"
#    global pic
#    pic.value = imgStr
#    pic.resize(200,200)
#    picBox.after(5000, testView)
    

# Configuration region (set up all configs for logic here)

config={
        "apiKey":"AIzaSyDzfkGMxxgE_Zy_Lzz5noDYicXlCVx-hJI",
        "authDomain": "todolist-d1359.firebaseapp.com",
        "databaseURL":"https://todolist-d1359.firebaseio.com",
        "storageBucket":"todolist-d1359.appspot.com"
    }

firebase = pyrebase.initialize_app(config)

db = firebase.database();

# End Configuration

app = App(title = "HAD Display")
app.set_full_screen()
baseBox = Box(app, width = "fill", height = "fill")
emptyTxt = Text(baseBox, text = "\n")
curDt = datetime.now()
currentDtTxt = Text(baseBox, text = "Welcome " + str(checkName()) + "!\n\n It is: " + curDt.strftime("%m-%d-%Y %I:%M %p"), size = 30)
currentDtTxt.after(1000, updateTime)

# To-Do View
todoBox = Box(baseBox, width = "fill", height = "fill")
todoTitle = Text(todoBox, text = "\nHere are the things you need to do:\n", size = 40)
todoTxt = Text(todoBox, text = "", size = 30)
todoBox.visible = False

# Calendar View
calBox = Box(baseBox, width = "fill", height = "fill")
calTitle = Text(calBox, text = "\nHere are some of the upcoming events you have:\n", size = 40)
calTxt = Text(calBox, text = "Nah Fam", size = 30)
calBox.visible = False

# Weather view
wthrBox = Box(baseBox, width = "fill", height = "fill")
wthrTitle = Text(wthrBox, text = "\nToday's weather", size = 40)
wthrInBox = Box(wthrBox, layout = "grid", width = "fill", height = "fill")
imgStr = "weather/thunderstorm-night.jpg"
weatherFile = imgStr.split("/")[1]
weatherTxt = weatherFile.split(".")[0]
wthrPic = Picture(wthrInBox, image = imgStr, grid = [0,0], align = "left")
wthrPic.resize(1200, 650)
wthrTxt = Text(wthrInBox, text = "  " + weatherTxt + "\n\n  Temp: 75\n\n  High: 77\n\n  Low: 66\n\n", size = 30, grid = [2,0], align = "right")
wthrBox.visible = False
switchView()
#testView()
app.display()
