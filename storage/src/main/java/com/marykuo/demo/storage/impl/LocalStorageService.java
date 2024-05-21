package com.marykuo.demo.storage.impl;

import com.marykuo.demo.storage.StorageService;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class LocalStorageService implements StorageService {

    /**
     * save file
     *
     * @param filePath test.txt same as ./test.txt
     */
    @Override
    public void saveFile(String filePath, byte[] binaryData) {
        assert filePath != null;
        assert binaryData != null && binaryData.length > 0;

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(binaryData);
            log.debug("writing file successfully. filePath [{}]", filePath);
        } catch (IOException e) {
            log.error("save file error", e);
            throw new RuntimeException("save file error, " + e.getMessage());
        }
    }

    /**
     * save csv file in utf-8 with BOM
     */
    @Override
    public void saveCsv(String filePath, List<String> datalist) {
        assert filePath != null;
        assert datalist != null && !datalist.isEmpty();

        try (Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(filePath)), StandardCharsets.UTF_8)) {
            // Write BOM
            writer.write('\ufeff');

            // Write data to CSV
            for (String data : datalist) {
                writer.write(data);
            }
            log.debug("writing CSV successfully. filePath [{}]", filePath);
        } catch (IOException e) {
            log.error("writing CSV error. [{}]", e.getMessage());
            throw new RuntimeException("save CSV error, " + e.getMessage());
        }
    }

    /**
     * create folder
     */
    @Override
    public void createDirectory(String folderPath) {
        assert folderPath != null;

        File folder = new File(folderPath);
        if (folder.exists()) {
            log.debug("Folder [{}] already exists", folderPath);
        } else if (!folder.exists() && folder.mkdirs()) {
            log.info("Creating folder at [{}] successfully", folderPath);
        } else {
            log.warn("Creating folder at [{}] Failed", folderPath);
        }
    }

    /**
     * set file read only
     *
     * @param filePath
     */
    public void setReadOnly(String filePath) {
        assert filePath != null;

        new File(filePath).setReadOnly();
    }

    /**
     * read file
     *
     * @param filePath
     * @return
     */
    @Override
    public byte[] readFile(String filePath) {
        assert filePath != null;

        log.debug("read file at path [{}]", filePath);
        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            log.error("read file at path [{}] has error: [{}]", filePath, e.getMessage());
            throw new RuntimeException("read file error, " + e.getMessage());
        }
    }

    /**
     * delete file or empty folder
     * <p>
     * throws java.nio.file.DirectoryNotEmptyException if directory is not empty
     */
    @Override
    public void delete(String filePath) {
        assert filePath != null;

        File file = new File(filePath);
        if (!file.exists()) {
            log.info("file not exist [{}]", filePath);
            return;
        }

        final String name = file.isDirectory() ? "folder" : "file";
        if (file.delete()) {
            log.debug("deleted {} successfully [{}]", name, filePath);
        } else {
            log.warn("failed to delete {} [{}]", name, filePath);
        }

    }

    /**
     * delete folder and all files in it
     */
    public void deleteDirectory(String folderPath) {
        assert folderPath != null;

        File folder = new File(folderPath);
        if (!folder.exists()) {
            log.info("folder not exist [{}]", folderPath);
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file.getPath());
                } else {
                    delete(file.getPath());
                }
            }
        }
        delete(folderPath);
    }

    private boolean isFileExist(final String filePath) {
        assert filePath != null;

        return new File(filePath).exists();
    }

    @Override
    public void zipFile(List<String> srcFilePathList, String zipFilePath) {
        assert srcFilePathList != null && !srcFilePathList.isEmpty();
        assert zipFilePath != null;

        try (FileOutputStream fos = new FileOutputStream(zipFilePath)) {
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            for (String srcFilepath : srcFilePathList) {
                zipFile(srcFilepath, zipOut);
            }
            zipOut.close();
            log.info("zipping files successfully. zipFilepath [{}]", zipFilePath);
        } catch (IOException e) {
            log.error("zip file error", e);
            throw new RuntimeException("zip file error, " + e.getMessage());
        }
    }

    private void zipFile(String srcFilePath, ZipOutputStream zipOut) {
        assert srcFilePath != null;
        assert zipOut != null;

        File srcFile = new File(srcFilePath);
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            ZipEntry zipEntry = new ZipEntry(srcFile.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            log.info("zipping file [{}]", srcFilePath);
        } catch (IOException e) {
            log.error("zip file error", e);
            throw new RuntimeException("zip file error, " + e.getMessage());
        }
    }
}
