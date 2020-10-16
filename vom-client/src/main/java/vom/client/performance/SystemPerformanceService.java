package vom.client.performance;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.Sensors;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.net.NetworkInterface;
import java.net.SocketException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemPerformanceService {

  private static final SystemInfo system = new SystemInfo();
  private static final HardwareAbstractionLayer hardware = system.getHardware();
  private static final FileSystem fileSystem =
    system.getOperatingSystem().getFileSystem();
  private static final NetworkIF[] networkInterfaces = hardware.getNetworkIFs();


  public static double getCpu() {
    return hardware.getProcessor().getSystemCpuLoad();
  }

  public static long[] getMemory() {
    final GlobalMemory memory = hardware.getMemory();
    return new long[]{
      memory.getAvailable(),
      memory.getTotal()
    };
  }

  public static long[] getDisk() {
    final OSFileStore[] fileStores = fileSystem.getFileStores();

    long total = 0L;
    long empty = 0L;

    for (OSFileStore store : fileStores) {
      total += store.getTotalSpace();
      empty += store.getUsableSpace();
    }

    return new long[]{
      empty,
      total
    };
  }

  public static long[] getNetwork() {
    long bandwidth = 0L;
    long sent = 0L;
    long received = 0L;

    for (final NetworkIF network : networkInterfaces) {
      NetworkInterface ni = network.getNetworkInterface();

      try {
        if (!ni.isVirtual() && !ni.isLoopback() && ni.isUp()) {
          bandwidth += network.getSpeed();

          final long beforeSent = network.getBytesSent();
          final long beforeReceived = network.getBytesRecv();
          final long timestamp = network.getTimeStamp();

          network.updateNetworkStats();
          final double seconds = (network.getTimeStamp() - timestamp) / 1000D;

          sent += (network.getBytesSent() - beforeSent) * 8 / seconds;
          received += (network.getBytesRecv() - beforeReceived) * 8 / seconds;
        }
      }
      catch (SocketException e) {
        e.printStackTrace(System.err);
      }
    }

    return new long[]{
      received,
      sent,
      bandwidth
    };
  }

  public static Object[] getSensors() {
    final Sensors sensors = hardware.getSensors();
    return new Object[]{
      sensors.getCpuTemperature(),
      sensors.getCpuVoltage(),
      sensors.getFanSpeeds()
    };
  }

}
