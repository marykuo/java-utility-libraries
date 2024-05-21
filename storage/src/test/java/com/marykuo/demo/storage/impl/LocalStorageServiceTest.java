package com.marykuo.demo.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class LocalStorageServiceTest {
    private LocalStorageService localStorageService;

    private static final String TEST_DIR = "files";

    @BeforeEach
    void setUp() {
        log.info("***** setUp *****");
        this.localStorageService = new LocalStorageService();
        localStorageService.createDirectory(TEST_DIR);
        log.info("***** setUp *****\n");
    }

    @AfterEach
    void tearDown() {
        System.out.print("\n");
        log.info("***** tearDown *****");
        localStorageService.deleteDirectory(TEST_DIR);
        log.info("***** tearDown *****");
    }

    @Test
    void whenSaveFileInRelativePathTest() {
        String filePath = TEST_DIR + "/test.txt";
        byte[] fileContent = "test".getBytes();

        localStorageService.saveFile(filePath, fileContent);
    }

    @Test
    @Disabled
    void whenSaveFileInAbsolutePathTest() {
        byte[] fileContent = "test".getBytes();

        localStorageService.saveFile("C:\\Users\\gateweb\\Downloads\\test4.txt", fileContent);
    }

    @Test
    void whenSaveCsvWithBomTest() {
        String filePath = TEST_DIR + "/csv_utf8_bom.csv";
        List<String> fileContent = List.of("1,2,3\n", "A,B,C\n", "壹,貳,參\n");

        localStorageService.saveCsv(filePath, fileContent);
    }

    @Test
    void deleteNotExistFileTest() {
        String filePath = TEST_DIR + "/test.txt";

        localStorageService.delete(filePath);
    }

    @Test
    void deleteExistFileTest() {
        // given
        String filePath = TEST_DIR + "/test.txt";
        byte[] fileContent = "test".getBytes();
        localStorageService.saveFile(filePath, fileContent);

        // when
        localStorageService.delete(filePath);
    }

    @Test
    void readFileTest() {
        // given
        String filePath = TEST_DIR + "/test.txt";
        byte[] fileContent = "test".getBytes();
        localStorageService.saveFile(filePath, fileContent);

        byte[] bytes = localStorageService.readFile(filePath);
        assertThat(bytes).isEqualTo(fileContent);
    }

    @Test
    void zipFileTest() {
        byte[] fileContent = "test".getBytes();
        localStorageService.saveFile(TEST_DIR + "/test_1.txt", fileContent);
        localStorageService.saveFile(TEST_DIR + "/test_2.txt", fileContent);
        localStorageService.saveFile(TEST_DIR + "/test_3.txt", fileContent);

        String zipFilePath = TEST_DIR + "/test.zip";
        List<String> filePathList = List.of(TEST_DIR + "/test_1.txt", TEST_DIR + "/test_2.txt", TEST_DIR + "/test_3.txt");
        localStorageService.zipFile(filePathList, zipFilePath);
    }
}