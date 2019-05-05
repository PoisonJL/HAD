#!/bin/sh

# If the wpa_supplicant.conf.orig file does not exist, this means that the device is in AP mode,
# so proceed to connect the device to a Wi-Fi network
# If it does exist, it is in Wi-Fi mode, so do not do anything.
# This is to avoid getting the Pi in a bad state, as this will modify important configuration files
# needed to connect to the network.

if ! [ -f "/etc/wpa_supplicant/wpa_supplicant.conf.orig" ]; then
	sudo mv /etc/wpa_supplicant/wpa_supplicant.conf /etc/wpa_supplicant/wpa_supplicant.conf.orig
	if [ -f "/home/pi/had/hadWifi.conf" ]; then
		sudo mv /home/pi/had/hadWifi.conf /etc/wpa_supplicant/wpa_supplicant.conf
	else
		sudo mv /etc/wpa_supplicant/wpa_supplicant.conf.mod /etc/wpa_supplicant/wpa_supplicant.conf
	fi
	sudo mv /etc/dhcpcd.conf /etc/dhcpcd.conf.mod
	sudo mv /etc/dhcpcd.conf.orig /etc/dhcpcd.conf
	sudo service hostapd stop
	sudo service dnsmasq stop
	sudo update-rc.d hostapd disable
	sudo update-rc.d dnsmasq disable
	sudo service dhcpcd restart
fi
