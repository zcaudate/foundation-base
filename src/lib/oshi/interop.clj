(ns lib.oshi.interop
  (:require [std.object :as object]))

(def ^:dynamic *platform* (str (oshi.SystemInfo/getCurrentPlatform)))

;; Hardware Abstractions
(object/map-like

 oshi.SystemInfo
 {:tag "system" :read :all}
 oshi.hardware.Baseboard
 {:tag "baseboard" :read :all}
 oshi.hardware.CentralProcessor
 {:tag "cpu" :read :all}
 oshi.hardware.CentralProcessor$LogicalProcessor
 {:tag "processor" :read :all}
 oshi.hardware.CentralProcessor$ProcessorIdentifier
 {:tag "id" :read :all}
 oshi.hardware.ComputerSystem
 {:tag "cs" :read :all}
 oshi.hardware.Display
 {:tag "display" :read :all}
 oshi.hardware.Firmware
 {:tag "firmware" :read :all}
 oshi.hardware.GlobalMemory
 {:tag "memory" :read :all}
 oshi.hardware.GraphicsCard
 {:tag "graphics" :read :all}
 oshi.hardware.HardwareAbstractionLayer
 {:tag "hardware" :read :all}
 oshi.hardware.HWPartition
 {:tag "disk.partition" :read :all}
 oshi.hardware.HWDiskStore
 {:tag "disk.store" :read :all}
 oshi.hardware.NetworkIF
 {:tag "net" :read :all}
 oshi.hardware.PhysicalMemory
 {:tag "memory.physical" :read :all}
 oshi.hardware.PowerSource
 {:tag "power" :read :all}
 oshi.hardware.Sensors
 {:tag "sensors" :read :all}
 oshi.hardware.SoundCard
 {:tag "sound" :read :all}
 oshi.hardware.UsbDevice
 {:tag "usb" :read :all}
 oshi.hardware.VirtualMemory
 {:tag "memory.virtual" :read :all})

(comment

  oshi.software.os.mac.MacOSThread

  oshi.hardware.Networks
  {:tag "networks" :read :all}

  oshi.hardware.Disks
  {:tag "disks" :read :all}

  oshi.software.os.OperatingSystemVersion
  {:tag "version" :read :all}
  oshi.software.os.OSUser
  {:tag "user" :read :all})

;; Software Abstractions

(object/map-like

 oshi.software.os.FileSystem
 {:tag "file" :read :all}
 oshi.software.os.InternetProtocolStats
 {:tag "ip.stats" :read :all}
 oshi.software.os.NetworkParams
 {:tag "net.params" :read :all}
 oshi.software.os.OSFileStore
 {:tag "fs" :read :all}
 oshi.software.os.OperatingSystem
 {:tag "os" :read :all}
 oshi.software.os.OperatingSystem$OSVersionInfo
 {:tag "os.info" :read :all}
 oshi.software.os.OSProcess
 {:tag "process" :read :all}
 oshi.software.os.OSService
 {:tag "service" :read :all}
 oshi.software.os.OSSession
 {:tag "session" :read :all}
 oshi.software.os.OSThread
 {:tag "thread" :read :all})
