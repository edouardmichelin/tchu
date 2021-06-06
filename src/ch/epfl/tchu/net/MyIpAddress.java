package ch.epfl.tchu.net;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Montre les adresses de l'ordinateur
 */
public final class MyIpAddress {
    public static List<String> show() {
        try {
            return NetworkInterface.networkInterfaces()
                    .filter(i -> {
                        try { return i.isUp() && !i.isLoopback(); }
                        catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .flatMap(NetworkInterface::inetAddresses)
                    .filter(a -> a instanceof Inet4Address)
                    .map(InetAddress::getCanonicalHostName)
                    .collect(Collectors.toUnmodifiableList());
        } catch (SocketException e) {
            throw new Error(e);
        }
    }
}