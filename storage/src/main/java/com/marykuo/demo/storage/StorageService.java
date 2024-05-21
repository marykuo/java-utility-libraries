package com.marykuo.demo.storage;

import java.util.List;

public interface StorageService {

    void saveFile(String filePath, byte[] binaryData);

    void saveCsv(String filePath, List<String> datalist);

    void createDirectory(String folderPath);

    void setReadOnly(String filePath);

    byte[] readFile(String filePath);

    void delete(String filePath);

    void deleteDirectory(final String folderPath);

    void zipFile(List<String> srcFilePathList, String zipFilePath);
}
