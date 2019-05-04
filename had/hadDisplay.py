from guizero import *
from datetime import datetime, time
import threading
import pyrebase
import requests
import json
import time
import subprocess
import glob
import urllib.request
#import os
#import socket

# Initialize Variables Region

# There is a glitch with the pyrebase library when dealing with quotes
# This will temporarily replace quote function to only add quotes once to API calls
def noquote(s):
    return s
pyrebase.pyrebase.quote = noquote

# Initializing global variables
curWifi = ""                    # Will contain the SSID the device is currently connected to
units = ""                      # Metric or imperial
curTemp = -255                  # Current temperature
curMaxTmp = -255                # High temperature
curMinTmp = -255                # Low temperature
curCondType = ""                # Type of weather conditions (cloudy, rainy, etc.)
curCond = ""                    # Actual weather description (scattered showers, etc.)
imgStr = ""                     # Will contain the link to the different images for the weather display
lastImgStr = ""                 # Will contain the last image link (needed to compare and determine when the weather picture needs to change)
isNight = False                 # Depending on the time, it will either be true (if it's between 9 PM and 6 AM) or False
changePic = False               # The condition that will be checked to see if the picture needs to be changed

# End Initialize Variables Region

# Functions Region

# Checks to see if this is the first time the device has been turned on or not
def checkForInitialStartup():
    initStart = str(glob.glob("/home/pi/had/startup"))
    if initStart == "[]":
        return False
    else:
        return True

# Updates the time each second
def updateTime():
    global curDt
    currentDtTxt.clear()
    curDt = datetime.now()
    currentDtTxt.append("Welcome " + str(checkName()) + "!\n\n It is: " + curDt.strftime("%m-%d-%Y %I:%M %p"))
    currentDtTxt.after(1000, updateTime)

# Displays all of the to-do lists, and checks whether privacy mode is on or off
def privacyMask():
    global curWifi
    global db
    conWifi = ""
    conWifiName = ""
    privacy = ""
    calList = ""
    # Optimistic - if there is an error, just continue cycling without issues
    try:
        conWifi = db.child("Connected WiFi").child("SSID").get()
        conWifiName = conWifi.val()
        print(conWifiName)
        privacy = db.child("Privacy Mode").child("Mode").get()
        todoList = db.child("ToDo List").child("Item").get()
        todoTxt.clear()
        try:
            for t in todoList.each():
                todoKey = t.key()
                todoItem = db.child("ToDo List").child("Item").child(todoKey).get().val()
                if privacy.val() == "Privacy Off" and curWifi == conWifiName:
                    todoName = todoItem
                    todoTxt.append(todoName + "\n")
                else:
                    todoTxt.append("Something to do\n")
        except TypeError:
            print("There are no items!\n")
    except:
        print("Error")
    if todoBox.visible == True:
        todoTxt.after(2000, privacyMask)
        
# Displays all of the calendar events, and checks whether privacy mode is on or off
def privacyCalMask():
    global curDt
    global curWifi
    global db
    conWifi = ""
    conWifiName = ""
    privacy = ""
    calList = ""
    try:
        conWifi = db.child("Connected WiFi").child("SSID").get()
        conWifiName = conWifi.val()
        privacy = db.child("Privacy Mode").child("Mode").get()
        calList = db.child("Tasks").order_by_child("dayname").get()
        calTxt.clear()
        try:
            calFile = open("/home/pi/had/hadCal.txt", "w+")
            calFile.write(str(privacy.val()) + "\n")
            for t in calList.each():
                calKey = t.key()
                print(calKey)
                calItem = db.child("Tasks").child(calKey).get().val()
                print(str(calItem['dayname']))
                calDate = datetime.strptime(str(calItem['dayname']), "%m/%d/%Y")
                if curDt <= calDate:
                    calFile.write(calItem['dayname'] + " - " + calItem['titlename'] + "\n")
                    if privacy.val() == "Privacy Off" and curWifi == conWifiName:
                        calTxt.append(calItem['dayname'] + " - " + calItem['titlename'] + "\n")
                    else:
                        calTxt.append("Something to do\n")
            calFile.close()
        except TypeError:
            print("There are no items!\n")
    except:
        calTxt.clear()
        print("Error")
        checkCalFile = str(glob.glob("/home/pi/had/hadCal.txt"))
        if checkCalFile == "[]":
            calTxt.append("No events!")
        else:
            calFile = open("/home/pi/had/hadCal.txt", "r")
            calLine = calFile.readlines()
            tmpPrivacy = True
            for l in calLine:
                if "Off" in l and curWifi == conWifiName:
                    tmpPrivacy = False
                else:
                    if tmpPrivacy == True:
                        calTxt.append("Something to do")
                    else:
                        calTxt.append(str(l)) 
            calFile.close()
    if calBox.visible == True:
        calTxt.after(2000, privacyCalMask)

# Displays the current weather information
def weatherInfo():
    global changePic
    global imgStr
    global wthrPic
    global curCond, curTemp, curMaxTmp, curMinTmp
    wthrTxt.clear()
    wthrTxt.append("  " + curCond.capitalize() + "\n\n")
    wthrTxt.append("  Temp: " + str(round(curTemp)) + "\n\n")
    wthrTxt.append("  High: " + str(round(curMaxTmp)) + "\n\n")
    wthrTxt.append("  Low: " + str(round(curMinTmp)) + "\n\n")
    if changePic == True:
        wthrPic.value = imgStr
        wthrPic.resize(1400, 800)
        changePic = False
    wthrTxt.after(120000, getLatestWeather)
    

# API Call to ipstack.com to get current location
# The location is then used to get the weather
def getIpAndSetLoc():
    print("Getting IP And Location")
    global lat, long
    global units
    getLoc = "http://api.ipstack.com/check?access_key=26f340ead375064984cfaccafb9aea87"
    locReq = requests.get(getLoc)
    if locReq.status_code != 200:
        print(locReq.status_code)
        checkLocFile = str(glob.glob("/home/pi/had/hadLoc.conf"))
        if checkLocFile == "[]":
            lat = 36
            long = -94
            units = "imperial"
        else:
            locFile = open("/home/pi/had/hadLoc.conf", "r")
            locLine = locFile.readlines()
            for l in locLine:
                if "units" in l:
                    units = l.split("=")[1]
                elif "lat" in l:
                    lat = l.split("=")[1]
                elif "long" in l:
                    long = l.split("=")[1]
            print(units + "; " + lat + "; " + long)
            locFile.close()
                    
    else:
        locData = json.loads(locReq.text)
        ipAddr = locData['ip']
        conf = open("/home/pi/had/hadLoc.conf", "w+")
        print(ipAddr)
        countryCode = locData['country_code']
        units = ""
        if countryCode == "US":
            units = "imperial"
        else:
            units = "metric"
        conf.write("units=" + units + "\n")
        lat = locData['latitude']
        conf.write("lat=" + str(lat) + "\n")
        long = locData['longitude']
        conf.write("long=" + str(long) + "\n")
        conf.close()
        # After getting the location (specifically, the latitude and longitude), get the latest weather
        print("Got IP And Location")
        getLatestWeather()

# Queries the weather API to get the latest weather information
def getLatestWeather():
    print("Getting Weather Info")
    global lat
    global long
    global units
    global imgStr, lastImgStr
    global curTemp, curMaxTmp, curMinTmp, curCondType, curCond, curDt
    global wthrPic
    global changePic
    weatLink = "http://api.openweathermap.org/data/2.5/weather?lat="+str(lat)+"&lon="+str(long)+"&units="+units+"&appid=39177e03ec521ba7e70353e7e3d960ee"
    print(weatLink)
    weatReq = requests.get(weatLink)
    if weatReq.status_code != 200:
        curTemp = "Weather service down"
        curCond = "This will be updated when service is up"
    else:
        weatData = json.loads(weatReq.text)
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
        imgStr = "/home/pi/had/weather/"
        if "Clear" in str(curCondType):
            imgStr = imgStr + "clearsky"
        elif "Clouds" in str(curCondType):
            if "few" in str(curCond):
                imgStr = imgStr + "fewclouds"
            elif "scattered" in str(curCond):
                imgStr = imgStr + "scatteredclouds"
            elif "broken" in str(curCond):
                imgStr = imgStr + "brokenclouds"
            elif "overcast" in str(curCond):
                imgStr = imgStr + "overcast"
            else:
                imgStr = imgStr + "fewclouds"
        elif "Drizzle" in str(curCondType):
            imgStr = imgStr + "showers"
        elif "Thunderstorm" in str(curCondType):
            imgStr = imgStr + "thunderstorm"
        elif "Rain" in str(curCondType):
            imgStr = imgStr + "rain"
        elif "Snow" in str(curCondType):
            imgStr = imgStr + "snow"
        elif "Fog" in str(curCondType) or "Mist" in str(curCondType):
            imgStr = imgStr + "fog"
        if isNight == True:
            imgStr = imgStr + "-night.jpg"
        else:
            imgStr = imgStr + "-day.jpg"
        print(imgStr)
        print(lastImgStr)
        if lastImgStr == imgStr:
            changePic = False
        else:
            changePic = True
            lastImgStr = imgStr
        print("Got Weather Info")

# Future work: Add a 5-day forecast display 
#foreLink = "http://api.openweathermap.org/data/2.5/forecast?lat="+str(lat)+"&lon="+str(long)+"&units="+units+"&cnt=10&appid=39177e03ec521ba7e70353e7e3d960ee"
#print(foreLink)
#foreReq = requests.get(foreLink)
#foreData = json.loads(foreReq.text)
    
# Checks the name of the user for a more personalized experience
# If no name is found, the default is set to "user"
# If time permits, add this functionality to phone
def checkName():
    global db
    firstName = ""
    ui = db.child("User Info").child("First Name").get()
    if ui.val() is None:
        firstName = "user"
    else:
        firstName = ui.val()
    return firstName

# Switches the displays every 10 seconds
# For future work, add the extra displays (boxes) here
def switchView():
    global locThread
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
    locThread.join()

# Defines the necessary configuration settings to connect to Firebase
def setupFirebaseConfig():
    config={
        "apiKey":"AIzaSyDzfkGMxxgE_Zy_Lzz5noDYicXlCVx-hJI",
        "authDomain": "todolist-d1359.firebaseapp.com",
        "databaseURL":"https://todolist-d1359.firebaseio.com",
        "storageBucket":"todolist-d1359.appspot.com"
        }
    firebase = pyrebase.initialize_app(config)
    return firebase.database()

# Scan for the Wi-Fi files sent from the phone
# If the files cannot be found, it will continue to scan for them until it finds them
# Otherwise, it will attempt to connect to the network. If it fails, it will revert back to an access point
def scanForWifiFiles():
    global curWifi
    global welText
    tmpId = str(glob.glob("/home/pi/ssid:*"));
    if tmpId == "[]":
        welText.after(1000, scanForWifiFiles)
    else:
        welText.clear()
        welText.value = "\n\n\n\n\n\n\n\nAttempting to connect to " + tmpId + "..."
        welText.append("\n(This could take up to 20 seconds)")
        print("Files received")
        tmpId = tmpId.split(" ", 1)[1]
        ssid = tmpId[0:len(tmpId) - 2]
        tmpPw = str(glob.glob("/home/pi/password:*"));
        tmpPw = tmpPw.split(" ", 1)[1]
        ssidPw = tmpPw[0:len(tmpPw) - 2]
        print(ssid)
        print(ssidPw)
        subprocess.call(["rm -rf /home/pi/ssid:* "], shell = True)
        subprocess.call(["rm -rf /home/pi/password:* "], shell = True)
        f = open("/home/pi/had/hadWifi.conf", "a+")
        f.write("ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev\n")
        f.write("update_config=1\n")
        f.write("country=US\n\n")
        f.write("network={\n")
        f.write("\tssid=\"" + ssid + "\"\n")
        f.write("\tpsk=\"" + ssidPw + "\"\n")
        f.write("\tkey_mgmt=WPA-PSK\n}\n\n")
        f.close()
        subprocess.call(["/home/pi/had/apToWifi.sh"], shell = True)
        time.sleep(15)
        curWifi = checkWifi()
        # If connecting to Wi-Fi failed (for one reason or another), checkWifi will return "Reinitialize", and the user must reenter the Wi-Fi credentials
        # Otherwise, begin to cycle through the displays
        if curWifi == "Reinitialize":
            welText.clear()
            welText.value = "\n\n\n\n\n\n\nConnection to internet failed.\n\nPlease connect the Home Ambient Displayer\nto the internet using the smartphone app"
            welText.after(2000, scanForWifiFiles)
        else:
            global db
            global locThread
            subprocess.call(["rm -rf /home/pi/had/startup"], shell = True)
            baseBox.visible = True
            initBox.visible = False
            currentDtTxt.after(1000, updateTime)
            db = setupFirebaseConfig()
            locThread = threading.Thread(target = getIpAndSetLoc, args = ())
            locThread.start()
            switchView()

# Check to see the network status. If it is connected to Wi-Fi, it will return the name of the SSID
# Otherwise, it will revert back to being an Access Point
def checkWifi():
    # Get WiFi
    wifi = ""
    try:
        wifi = str(subprocess.check_output(["/sbin/iwgetid -r"], shell = True).rstrip())
        if wifi[0] == 'b' and wifi[1] == "\'":
            wifi = wifi.replace("b", "", 1)
        wifi = wifi[1:len(wifi) - 1]
    except subprocess.CalledProcessError:
        # This will always occur if the device is in Access Point Mode. Rather than try to switch it from Wi-Fi to AP, just proceed
        print("iwgetid didn't work. Reset the device!")
        subprocess.call(["/home/pi/had/wifiToAp.sh"], shell = True)
        return "Reinitialize"
    if wifi == "":
        print("Unable to connect. Reset the device!")
        subprocess.call(["/home/pi/had/wifiToAp.sh"], shell = True)
        return "Reinitialize"
    else:
        print(wifi)
        return wifi

# End Functions Region

# Main Region

# Initialize the window, but hide any and all displays until the device is certain that it is connected to the Wi-Fi
app = App(title = "HAD Display")
app.set_full_screen()
initBox = Box(app, width = "fill", height = "fill")
welText = Text(initBox, text = "\n\n\n\n\n\n\nWelcome!\n\nPlease connect the Home Ambient Displayer\nto the internet using the smartphone app", size = 40)
baseBox = Box(app, width = "fill", height = "fill")
curDt = datetime.now()
currentDtTxt = Text(baseBox, text = "\nWelcome!\n\n It is: " + curDt.strftime("%m-%d-%Y %I:%M %p"), size = 30)

# To-Do View
todoBox = Box(baseBox, width = "fill", height = "fill")
todoTitle = Text(todoBox, text = "\nHere are the things you need to do:\n", size = 40)
todoTxt = Text(todoBox, text = "", size = 30)
todoBox.visible = False

# Calendar View
calBox = Box(baseBox, width = "fill", height = "fill")
calTitle = Text(calBox, text = "\nHere are some of the upcoming events you have:\n", size = 40)
calTxt = Text(calBox, text = "", size = 30)
calBox.visible = False

# Weather view
wthrBox = Box(baseBox, width = "fill", height = "fill")
wthrTitle = Text(wthrBox, text = "\nToday's weather", size = 40)
wthrInBox = Box(wthrBox, layout = "grid", width = "fill", height = "fill")
imgStr = "/home/pi/had/weather/thunderstorm-night.jpg"
weatherFile = imgStr.split("/")[1]
weatherTxt = weatherFile.split(".")[0]
wthrPic = Picture(wthrInBox, image = imgStr, grid = [0,0], align = "left")
wthrPic.resize(1400, 850)
wthrTxt = Text(wthrInBox, text = "  " + weatherTxt + "\n\n  Temp: 75\n\n  High: 77\n\n  Low: 66\n\n", size = 30, grid = [2,0], align = "right")
wthrBox.visible = False

# Check to see if the device is starting up for the first time. If it is, display a welcome screen prompting for instructions
# If it is not the first time, check to see if there is a Wi-Fi connection. If there is, begin to display information.
# Otherwise, prompt them to connect the device to the internet with their smartphone
if checkForInitialStartup() == True:
    initBox.visible = True
    baseBox.visible = False
    scanForWifiFiles()
else:
    curWifi = checkWifi()
    if curWifi == "Reinitialize":
        initBox.visible = True
        welText.clear()
        welText.value = "\n\n\n\n\n\n\nConnection to internet failed.\n\nPlease connect the Home Ambient Displayer\nto the internet using the smartphone app"
        baseBox.visible = False
        scanForWifiFiles()
    else:
        baseBox.visible = True
        initBox.visible = False
        currentDtTxt.after(1000, updateTime)
        db = setupFirebaseConfig()
        locThread = threading.Thread(target = getIpAndSetLoc, args = ())
        locThread.start()
        switchView()
        
# This handles the actual creation of the window
app.display()

# End Main Region
