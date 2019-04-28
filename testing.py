from guizero import *
from datetime import *
import socket
import requests
import json

testIP="8.8.8.8"
s=socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
s.connect((testIP,0))
ipaddr=s.getsockname()[0]
host=socket.gethostname()

getLoc="http://api.ipstack.com/check?access_key=26f340ead375064984cfaccafb9aea87"

msg="Nothing"

resp=requests.get(getLoc)
if resp.status_code != 404 or resp.status_code != 101 or resp.status_code != 102 \
or resp.status_code != 103 or resp.status_code != 104 or resp.status_code != 105 \
or resp.status_code != 301 or resp.status_code != 302 or resp.status_code != 303:
    msg="Good so far!"
else:
    msg="Something's wrong!"
    print(resp.status_code)

print(msg)

jsonData = json.loads(resp.text)

ipDat=jsonData['ip']

print(jsonData['ip'])

country=jsonData['country_code']
units=""
if country=="US":
    units="imperial"
else:
    units="metric"
lat = jsonData['latitude']
print(str(lat))
long = jsonData['longitude']
print(str(long))

fLink="http://api.openweathermap.org/data/2.5/forecast?lat="+str(lat)+"&lon="+str(long)+"&units="+units+"&cnt=10&appid=39177e03ec521ba7e70353e7e3d960ee"
print(fLink)
fResp=requests.get(fLink)
fData=json.loads(fResp.text)

wLink="http://api.openweathermap.org/data/2.5/weather?lat="+str(lat)+"&lon="+str(long)+"&units="+units+"&appid=39177e03ec521ba7e70353e7e3d960ee"
print(wLink)
wResp=requests.get(wLink)
wData = json.loads(wResp.text)

for info in fData['list']:
    print(datetime.fromtimestamp(int(info['dt'])).strftime("%m-%d-%Y %H:%M"))
    print("Temp: " + str(info['main']['temp']))
    print("Weather type: " + str(info['weather'][0]['main']))
    print("Weather: " + str(info['weather'][0]['description']))

app=App(title="Testing")
text=Text(app, text="Country: ")
text.append(ipDat)
text.append(", Location: ")
text.append(host)
app.display()
