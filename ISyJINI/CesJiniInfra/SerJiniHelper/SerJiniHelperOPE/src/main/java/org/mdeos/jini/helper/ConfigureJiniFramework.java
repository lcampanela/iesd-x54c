package org.mdeos.jini.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;

import org.apache.commons.io.FilenameUtils;
import org.osgi.framework.BundleContext;

public class ConfigureJiniFramework {

    private static final Logger LOGGER = Logger.getLogger(ConfigureJiniFramework.class.getName());

    public static void main(String[] args) {
        try {
            String host = getIpv4NonLoopbackHostName();
            System.out.println("Host adress: " + host);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Config directory inside jar file:
     */
    public static final String JINI_CONFDIR = "config";

    /**
     * location where to copy config directory
     */
    public static final String JINI_RUNTIME_BASEPATH = System.getProperty("user.home") + "/" + ".jini";
    /**
     * Config directory inside jar file
     */
    public static final String JINI_RUNTIME_CONFDIR = JINI_RUNTIME_BASEPATH + "/" + JINI_CONFDIR;

    /**
     * Config directory for the CODEBASE mechanism (remote classpath) at
     * user.home. A root POM should define the property
     * <codebase.dir>${user.home}/.jini/www/classes</codebase.dir> to be used by
     * maven-resources-plugin to copy the required class files (commonly api)
     */
    public static final String JINI_RUNTIME_CODEBASE_DIR = JINI_RUNTIME_BASEPATH + "/" + "www/";

    public static void initJiniConfigFiles(BundleContext context) {
        Path destinationPath = Paths.get(JINI_RUNTIME_BASEPATH);
        try {
            Files.createDirectories(destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.FINEST, "Copying files..." + destinationPath);
        Enumeration<String> resourceList = context.getBundle().getEntryPaths("/" + JINI_CONFDIR);
        while (resourceList.hasMoreElements()) {
            URL resource = context.getBundle().getResource(resourceList.nextElement());
            String fileName = FilenameUtils.getName(resource.getPath());
            copyFiles(resource, destinationPath, fileName);
        }
    }

    private static void copyFiles(URL url, Path path, String fileName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        LOGGER.log(Level.FINEST, "Copy file: " + url.getFile());
        try {
            inputStream = url.openConnection().getInputStream();
            outputStream = new FileOutputStream(path.toString() + "/" + fileName);
            copyInputStreamToFile(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                try {
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    private static void copyInputStreamToFile(InputStream in, OutputStream out) {
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getIpv4NonLoopbackAddress() throws SocketException {
        InetAddress inetAddressFounded = null;
        boolean returnIpv4 = true; // to be parameterised if justifiable...
        boolean returnIPv6 = false;
        Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
        while (networkInterface.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) networkInterface.nextElement();
            for (Enumeration<InetAddress> inetAddress = i.getInetAddresses(); inetAddress.hasMoreElements();) {
                inetAddressFounded = (InetAddress) inetAddress.nextElement();
                if (!inetAddressFounded.isLoopbackAddress()) {
                    if (returnIpv4 && inetAddressFounded instanceof Inet4Address) {
                        return inetAddressFounded.getHostAddress();
                    } else if (returnIPv6 && inetAddressFounded instanceof Inet6Address) {
                        return inetAddressFounded.getHostAddress();
                    }
                }
            }
        }
        throw new IllegalStateException("No Found NonLoopbackAddress/IP");
    }

    public static String getIpv4NonLoopbackHostName() throws SocketException {
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getCanonicalHostName();
            LOGGER.log(Level.FINEST, "getCanonicalHostName: " + hostname);
            return hostname;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Exception obtaining Hostname ", ex);
        }
        throw new IllegalStateException("No Found NonLoopbackHostName");
    }

    public static void setSecurity(String policyFile) {
        System.setProperty("java.security.policy", policyFile);
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
    }

    public static void setServerCodebase() {
        try {
            String hostName = getIpv4NonLoopbackHostName();
            String codebase = "http://" + hostName + ":8080/classes/ file:" + JINI_RUNTIME_CODEBASE_DIR + "classes/";
            System.setProperty("java.rmi.server.codebase", codebase);
            LOGGER.log(Level.FINEST, "set java.rmi.server.codebase = " + codebase);
        } catch (SocketException ex) {
            LOGGER.log(Level.WARNING, "EXECEPTION setting java.rmi.server.codebase", ex);
        }
    }

    public static void enableRemoteCodebase() {
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    }

    public static URL findResourceURL(String filename) throws IOException {
        Objects.requireNonNull(filename, "filename param must not be null");
        return findResourceURL(ConfigureJiniFramework.class.getClassLoader(), filename);
    }

    public static URL findResourceURL(ClassLoader classLoader, String filename) throws IOException {
        Objects.requireNonNull(classLoader, "classLoader param must not be null");
        Objects.requireNonNull(filename, "filename param must not be null");

        URL resourceUrl = classLoader.getResource(filename);
        if (resourceUrl == null) {
            if (filename.startsWith("/")) {
                filename = filename.substring(1);
            }

            resourceUrl = classLoader.getResource(filename);
        }
        return resourceUrl;
    }

    public static InputStream findResourceStream(String filename) throws IOException {
        Objects.requireNonNull(filename, "filename param must not be null");
        return findResourceStream(ConfigureJiniFramework.class.getClassLoader(), filename);
    }

    public static InputStream findResourceStream(ClassLoader classLoader, String filename) throws IOException {
        Objects.requireNonNull(classLoader, "classLoader must not be null");
        Objects.requireNonNull(filename, "filename must not be null");

        InputStream resourceStream = classLoader.getResourceAsStream(filename);
        if (resourceStream == null) {
            // strip leading "/" otherwise stream from JAR wont work
            if (filename.startsWith("/")) {
                filename = filename.substring(1);
            }
            resourceStream = classLoader.getResourceAsStream(filename);
        }
        return resourceStream;
    }

    /**
     *
     * @param sourceEmbeddedDir directory inside jar
     * @param destFileSystemDir directory on computer file systems
     * @throws IOException
     * @throws URISyntaxException
     */
    public static void copyEmbeddedDirToFileSystemDir(String sourceEmbeddedDir, String destFileSystemDir)
            throws IOException, URISyntaxException {
        Objects.requireNonNull(sourceEmbeddedDir, "sourceEmbeddedDir param must not be null");
        Objects.requireNonNull(destFileSystemDir, "destFileSystemDir param must not be null");
        URI configFolder = new URI(ConfigureJiniFramework.findResourceURL(sourceEmbeddedDir).toString());
        File sourceDirectory = new File(configFolder);
        File dstDirectory = new File(destFileSystemDir);

        FileUtils.copyDirectoryToDirectory(sourceDirectory, dstDirectory);

        LOGGER.log(Level.FINEST, "COPY directory from: " + sourceDirectory.getAbsolutePath() + ", to: "
                + dstDirectory.getAbsolutePath());
    }

    /**
     * Copy a default embedded directory 'config' to a default filesystem
     * location FROM directory inside jar to ${user.home}/.jini/config
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    public static void copyDefaulEmbeddedDirToDefaultFileSystemDir() throws URISyntaxException, IOException {
        copyEmbeddedDirToFileSystemDir(ConfigureJiniFramework.JINI_CONFDIR,
                ConfigureJiniFramework.JINI_RUNTIME_BASEPATH);
    }

    public static void extractEmbeddedFilesToFileSystem(String embeddedConfDir, String runtimeConfigDir) {
        extractEmbeddedFilesToFileSystem(ConfigureJiniFramework.class.getClassLoader(), embeddedConfDir,
                runtimeConfigDir);
    }

    public static void extractEmbeddedFilesToFileSystem(ClassLoader classLoader, String embeddedDir,
            String runtimeConfigDir) {

        Objects.requireNonNull(classLoader, "classLoader param must not be null");
        Objects.requireNonNull(embeddedDir, "embeddedConfDir param must not be null");
        Objects.requireNonNull(runtimeConfigDir, "runtimeConfigDir param must not be null");

        try {
            /* "/resources" */
            URL embURL = ConfigureJiniFramework.findResourceURL(classLoader, embeddedDir);
            Objects.requireNonNull(embURL, "embURL must not be null, try to get embedded resource: " + embeddedDir);

            URI uri = embURL.toURI();

            LOGGER.finest("Copy embedded file from: " + embURL);

            Path myPath = null;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = null;
                boolean foundFS = false;
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                    myPath = fileSystem.getPath(embeddedDir);
                    foundFS = true;
                    LOGGER.finest("Filesystem : " + fileSystem.toString());
                } catch (FileSystemNotFoundException ex1) {
                    LOGGER.log(Level.WARNING, "FileSystemNotFoundException getFilesystem: " + uri);
                }

                if (foundFS == false) {
                    try {
                        fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap(),
                                classLoader);
                        myPath = fileSystem.getPath(embeddedDir);
                        LOGGER.finest("Filesystem : " + fileSystem.toString());
                    } catch (FileSystemAlreadyExistsException ex) {
                        LOGGER.log(Level.WARNING, "FileSystemAlreadyExistsException while newFilesystem: " + uri);
                    }
                }
            } else {
                // myPath = FileSystems.getDefault().getPath(embURL.toString());
                myPath = Paths.get(uri);
            }
            // TODO CHECK myPath null

            Stream<Path> walk = Files.walk(myPath, 1);
            for (Iterator<Path> it = walk.iterator(); it.hasNext();) {

                try {
                    URI sourceFileURI = it.next().toUri();

                    LOGGER.finest("Copy embedded file from: " + sourceFileURI);

                    ConfigureJiniFramework.createDirIfNotExist(runtimeConfigDir);
                    String str = sourceFileURI.toString();
                    String fileName = FilenameUtils.getName(str);

                    Path destinationPath = Paths.get(runtimeConfigDir);
                    URL resourceUrl = ConfigureJiniFramework.findResourceURL(classLoader, embeddedDir + "/" + fileName);
                    // NO else because expect never to get a null resource here, BUT....

                    if (resourceUrl != null) {
                        ConfigureJiniFramework.copyFile(resourceUrl, destinationPath, fileName);
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Exception copy file: ", ex);
                }
                /*
				 * // IF NOT A JAR(e.g RUN FROM IDE) if (!isInsideJar) {
				 * 
				 * File sourceFile = new File(sourceFileURI); if (sourceFile.isFile()) { File
				 * dstDirectory = new File(runtimeConfigDir); try {
				 * FileUtils.copyFileToDirectory(sourceFile, dstDirectory); } catch (IOException
				 * ex) { LOGGER.log(Level.SEVERE, "Exception copy file " + sourceFile + " to: "
				 * + dstDirectory, ex); } } } else { //WAS THE UNCOMMENTED CODE }
                 */
            }
            walk.close();
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, "Exception copy files from: " + embeddedDir, ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception copy files from:  " + embeddedDir, ex);
        }
    }

    public static void createDirIfNotExist(String dirPath) {
        Objects.requireNonNull(dirPath, "dirPath param must not be null");

        Path destinationDirectory = Paths.get(dirPath);
        if (!Files.exists(destinationDirectory)) {
            try {
                Files.createDirectories(destinationDirectory);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Exeception while try to create directory " + destinationDirectory, ex);
            }
        }
    }

    public static void copyFile(URL originFileurl, Path destinationPath, String fileName) {

        Objects.requireNonNull(originFileurl, "originFileurl param must not be null");
        Objects.requireNonNull(destinationPath, "destinationPath param must not be null");
        Objects.requireNonNull(fileName, "fileName param must not be null");

        InputStream inputStream = null;
        OutputStream outputStream = null;
        String destFile = destinationPath.toString() + "/" + fileName;

        LOGGER.log(Level.FINEST, "Copy file to : " + destFile);
        try {
            if (/* new File(originFileurl.toURI()).isFile() && new File(destFile).isFile() */true) {
                inputStream = originFileurl.openConnection().getInputStream();
                outputStream = new FileOutputStream(destFile);

                copyInputStreamToFile(inputStream, outputStream);
            }
        } catch (FileNotFoundException fln) {
            // SILENT
            // LOGGER.log(Level.WARNING, "FileNotFoundException while read/write
            // inputstream/ouputstream: ", fln);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "IOException while read/write inputstream/ouputstream: ", ex);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Exception while close FileStream: ", ex);
            }
        }
    }

    public static void setServerHostname() {
        try {
            setServerHostname(getIpv4NonLoopbackAddress());
        } catch (SocketException ex) {
            LOGGER.log(Level.WARNING, "Exception while setting ServerHostname: ", ex);
        }
    }

    public static void setServerHostname(String hostname) {
        Objects.requireNonNull(hostname, "hostname param must not be null");
        System.setProperty("java.rmi.server.hostname", hostname);
    }

    public static void setupLoggingConfig() {
        try {
            // Load a properties file from class path
            // alternative -Djava.util.logging.config.file
            /* */
            final LogManager logManager = LogManager.getLogManager();
            InputStream is = null;
            is = ConfigureJiniFramework.findResourceStream("/logging.properties");
            if (is != null) {
                logManager.readConfiguration(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
