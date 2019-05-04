#!/bin/sh

# If the wpa_supplicant.conf.orig file exists, this means the device is in Wi-Fi mode, so proceed
# to turn the device into an Access Point
# If it does not exist, this means that the device is already an Access Point, so do not do anything.
# This is to avoid getting the Pi in a bad state, as this will modify important configuration files
# needed to serve as an access point

if [ -f "/etc/wpa_supplicant/wpa_supplicant.conf.orig" ]; then
	sudo mv /etc/wpa_supplicant/wpa_supplicant.conf /etc/wpa_supplicant/wpa_supplicant.conf.mod
	sudo mv /etc/wpa_supplicant/wpa_supplicant.conf.orig /etc/wpa_supplicant/wpa_supplicant.conf
	sudo mv /etc/dhcpcd.conf /etc/dhcpcd.conf.orig
	sudo mv /etc/dhcpcd.conf.mod /etc/dhcpcd.conf
	sudo service dhcpcd restart
	sudo service hostapd start
	sudo service dnsmasq start
	sudo update-rc.d hostapd enable
	sudo update-rc.d dnsmasq enable
fi
