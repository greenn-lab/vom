package vom.client.performance;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.Sensors;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SystemResourceUsage {
  
  public static void main(String[] args) throws InterruptedException {
    final SystemInfo system = new SystemInfo();
    final HardwareAbstractionLayer hardware = system.getHardware();
    final FileSystem fileSystem = system.getOperatingSystem().getFileSystem();
  
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          System.out.printf("CPU Load: %f\n", hardware.getProcessor().getSystemCpuLoad());
          try {
            TimeUnit.SECONDS.sleep(1);
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
    
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          GlobalMemory memory = hardware.getMemory();
          System.out.printf("MEM     : %d / %d (%d)\n",
              memory.getAvailable(), memory.getTotal(), 100 - (memory.getAvailable() * 100 / memory.getTotal()));
      
          try {
            TimeUnit.SECONDS.sleep(1);
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
    
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          OSFileStore[] fileStores = fileSystem.getFileStores();
      
          long total = 0L;
          long empty = 0L;
      
          for (OSFileStore store : fileStores) {
            total += store.getTotalSpace();
            empty += store.getUsableSpace();
          }
      
          System.out.printf("DISK    : %d / %d (%d)\n", empty, total, total - empty);
      
          try {
            TimeUnit.SECONDS.sleep(1);
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
    
    final NetworkIF[] networkIFs = hardware.getNetworkIFs();
    
    new Thread(new Runnable() {
      private long[] getBandwidthAndBPS() throws SocketException {
        long[] result = {
            0L, // bandwidth
            0L, // sent
            0L  // received,
        };
  
        for (final NetworkIF network : networkIFs) {
          NetworkInterface ni = network.getNetworkInterface();
          if (!ni.isVirtual() && !ni.isLoopback() && ni.isUp()) {
            // result[0] += network.getSpeed();
            
            long sent = network.getBytesSent();
            long received = network.getBytesRecv();
            long timestamp = network.getTimeStamp();

            network.updateNetworkStats();
            double seconds = (network.getTimeStamp() - timestamp) / 1000D;
            
            result[1] += (network.getBytesSent() - sent) * 8 / seconds;
            result[2] += (network.getBytesRecv() - received) * 8 / seconds;
          }
        }
        
        return result;
      }
      
      @Override
      public void run() {
        while (true) {
          try {
            long[] bandwidthAndBPS = getBandwidthAndBPS();
            System.out.printf("NETWORK : BANDWIDTH %d, SENT %d, RECEIVED %d\n",
                bandwidthAndBPS[0],
                bandwidthAndBPS[1],
                bandwidthAndBPS[2]);
  
            TimeUnit.SECONDS.sleep(1);
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
    
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          final Sensors sensors = hardware.getSensors();
          System.out.printf("SENSORS : TEMPERATURE %f, VOLTAGE: %f, FANSPEED: %s\n",
              sensors.getCpuTemperature(),
              sensors.getCpuVoltage(),
              Arrays.toString(sensors.getFanSpeeds()));
      
          try {
            TimeUnit.SECONDS.sleep(1);
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }).start();
  
  
    //noinspection InfiniteLoopStatement
    while (true) {
      TimeUnit.SECONDS.sleep(1);
    }
  }
}
