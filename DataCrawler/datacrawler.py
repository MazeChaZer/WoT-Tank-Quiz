#This script needs BeautifulSoup 4 and curl
import urllib.request
from bs4 import BeautifulSoup
import re
from subprocess import call

soup = BeautifulSoup(urllib.request.urlopen("http://worldoftanks.eu/encyclopedia/vehicles/"))

countryconstants = {'Soviet Vehicles' : 'USSR',
                    'German Vehicles' : 'GERMANY',
                    'USA Vehicles' : 'USA',
                    'French Vehicles' : 'FRANCE',
                    'UK Vehicles' : 'UK',
                    'Chinese Vehicles' :'CHINA',
                    'Japanese Vehicles' : 'JAPAN'}

tankclassconstants = {'Light Tanks' : 'LIGHTTANK',
                      'Medium Tanks' : 'MEDIUMTANK',
                      'Heavy Tanks' : 'HEAVYTANK',
                      'Tank Destroyers' : 'TANKDESTROYER',
                      'SPGs' : 'SPG'}

countrylis = soup.select("ul.menu-sidebar-list > li")
xml = '<?xml version="1.0" encoding="utf-8"?>' + "\n" + '<tanks>' + "\n"
for countryli in countrylis:
  country = countryconstants[countryli.a.string]
  tankclasslis = countryli.select("ul > li.sub")
  for tankclassli in tankclasslis:
    tankclass = tankclassconstants[tankclassli.a.string]
    tanklis = tankclassli.select("li")
    for tankli in tanklis:
      tankname = tankli.select("a > span.b-vehicles_name")[0].string
      result = re.search('\/([^\/]*)\/([^\/]*)\/$', tankli.a['href'])
      imagename = result.group(1) + "-" + result.group(2) + ".png"
      escapedimagename = imagename.replace("-", "_") #File names in android resources mustn't contain a "-"
      xml += '    <tank name="' + tankname + '" pic="' + escapedimagename[:-4] + '" country="' + country + '" class="' + tankclass + '" />' + "\n"
      ##Uncomment the following line to download all tank images to the "images" folder
      #call(["curl", "-o", "images/"+escapedimagename, "--create-dirs", "http://worldoftanks.eu/static/3.16.0.2/encyclopedia/tankopedia/vehicle/"+imagename])
xml += '</tanks>'
print("Writing data to tanks.xml...")
f = open("tanks.xml","w")
f.write(xml)
f.close()