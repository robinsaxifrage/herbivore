package herbivore.res;
import herbivore.misc.Logger;
import herbivore.misc.ShutdownHook;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * a class representing an archive file to
 * load resources from
 * @author herbivore
 */
public class ArchiveFile {
    
    /**
     * creates a new archive file from the specified resource
     * @param resource the resource to use
     */
    protected ArchiveFile(Resource resource){
        try {
            File file = resource.getResourceFile();
            name = file.getName();
            path = file.getPath();
            archive = new JarFile(file);
            ShutdownHook.Static.addClose(archive);
        }
        catch (IOException exception){
            Logger.error(exception, "failed to load archive " + path);
        }
    }
    
    /**
     * extracts an archive inside this archive to a temporary location
     * (its like inception all fucking over again)
     * @param entry the entry to extract
     * @return the resource representing the extracted archive
     */
    protected Resource extractArchiveToTemp(JarEntry entry){
        String location = "res/temp/" + name + "-" + entry.getName();
        File destination = LoadUtils.getFile(location);
        if (!destination.exists()){
            destination.getParentFile().mkdir();
            destination.getParentFile().deleteOnExit();
            if (!entry.isDirectory()){
                try {
                    destination.createNewFile();
                    int bufferSize = 2048;
                    try (BufferedInputStream inputStream = new BufferedInputStream(archive.getInputStream(entry));
                            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destination), bufferSize)) {
                        int currentByte;
                        byte[] dataBuffer = new byte[bufferSize];
                        while ((currentByte = inputStream.read(dataBuffer, 0, bufferSize)) != -1) {
                            outputStream.write(dataBuffer, 0, currentByte);
                        }
                        outputStream.flush();
                    }
                }
                catch (IOException exception){
                    Logger.error(exception, "failed to unzip archive " + name + " at entry " + entry.getName());
                }
            }
        }
        return new StandardResource(location).loadAsArchiveFile();
    }
    
    /**
     * @return the base resource of this archive
     */
    public Resource getBaseResource(){
        return new ArchiveResource(this, null, false);
    }
    
    /**
     * finds the entry of the specified name in this archive
     * @param name the name of the entry to find
     * @return the entry, or null if it was not found
     */
    protected JarEntry getEntry(String name){
        Enumeration<JarEntry> entries = archive.entries();
        while (entries.hasMoreElements()){
            JarEntry entry = entries.nextElement();
            if (entry.getName().equals(name)){
                return entry;
            }
        }
        return null;
    }
        
    protected JarFile getArchive(){return archive;}
    protected String getName(){return name;}
    protected String getPath(){return path;}
        
    private JarFile archive;
    private String name, path;
}
