package com.data.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Apache Commons IO 文件工具类示例
 * 需要引入依赖：
 * <dependency>
 *     <groupId>commons-io</groupId>
 *     <artifactId>commons-io</artifactId>
 *     <version>2.11.0</version>
 * </dependency>
 */
public class TarGzUtil {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 读取文件内容到字符串
     *
     * @param filePath 文件路径
     * @return 文件内容
     * @throws IOException 读取异常
     */
    public static String readFileToString(String filePath) throws IOException {
        File file = new File(filePath);
        return FileUtils.readFileToString(file, DEFAULT_CHARSET);
    }

    /**
     * 读取文件内容到字符串（指定编码）
     *
     * @param filePath 文件路径
     * @param charset  字符编码
     * @return 文件内容
     * @throws IOException 读取异常
     */
    public static String readFileToString(String filePath, String charset) throws IOException {
        File file = new File(filePath);
        return FileUtils.readFileToString(file, charset);
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  内容
     * @throws IOException 写入异常
     */
    public static void writeStringToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, content, DEFAULT_CHARSET);
    }

    /**
     * 将字符串追加到文件末尾
     *
     * @param filePath 文件路径
     * @param content  内容
     * @throws IOException 写入异常
     */
    public static void appendStringToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        FileUtils.writeStringToFile(file, content, DEFAULT_CHARSET, true);
    }

    /**
     * 读取文件所有行到列表
     *
     * @param filePath 文件路径
     * @return 行列表
     * @throws IOException 读取异常
     */
    public static List<String> readLines(String filePath) throws IOException {
        File file = new File(filePath);
        return FileUtils.readLines(file, DEFAULT_CHARSET);
    }

    /**
     * 逐行读取文件内容
     *
     * @param filePath 文件路径
     * @throws IOException 读取异常
     */
    public static void readLinesWithIterator(String filePath) throws IOException {
        LineIterator it = FileUtils.lineIterator(new File(filePath), DEFAULT_CHARSET.name());
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                // 处理每一行
                System.out.println(line);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

    /**
     * 复制文件
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @throws IOException 复制异常
     */
    public static void copyFile(String sourcePath, String targetPath) throws IOException {
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);
        FileUtils.copyFile(sourceFile, targetFile);
    }

    /**
     * 复制文件到目录
     *
     * @param sourcePath 源文件路径
     * @param targetDir  目标目录
     * @throws IOException 复制异常
     */
    public static void copyFileToDirectory(String sourcePath, String targetDir) throws IOException {
        File sourceFile = new File(sourcePath);
        File targetDirectory = new File(targetDir);
        FileUtils.copyFileToDirectory(sourceFile, targetDirectory);
    }

    /**
     * 复制目录
     *
     * @param sourceDir 源目录
     * @param targetDir 目标目录
     * @throws IOException 复制异常
     */
    public static void copyDirectory(String sourceDir, String targetDir) throws IOException {
        File sourceDirectory = new File(sourceDir);
        File targetDirectory = new File(targetDir);
        FileUtils.copyDirectory(sourceDirectory, targetDirectory);
    }

    /**
     * 删除文件或目录
     *
     * @param path 文件或目录路径
     * @throws IOException 删除异常
     */
    public static void deleteFileOrDirectory(String path) throws IOException {
        File file = new File(path);
        FileUtils.delete(file);
    }

    /**
     * 强制删除文件或目录（即使非空目录也会删除）
     *
     * @param path 文件或目录路径
     * @throws IOException 删除异常
     */
    public static void forceDelete(String path) throws IOException {
        File file = new File(path);
        FileUtils.forceDelete(file);
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录路径
     * @throws IOException 创建异常
     */
    public static void createDirectory(String dirPath) throws IOException {
        File directory = new File(dirPath);
        FileUtils.forceMkdir(directory);
    }

    /**
     * 获取文件扩展名
     *
     * @param filePath 文件路径
     * @return 扩展名
     */
    public static String getFileExtension(String filePath) {
        return FilenameUtils.getExtension(filePath);
    }

    /**
     * 获取文件名（不包含路径）
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileName(String filePath) {
        return FilenameUtils.getName(filePath);
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param filePath 文件路径
     * @return 不带扩展名的文件名
     */
    public static String getBaseName(String filePath) {
        return FilenameUtils.getBaseName(filePath);
    }

    /**
     * 获取文件路径（不包含文件名）
     *
     * @param filePath 文件路径
     * @return 路径
     */
    public static String getFullPath(String filePath) {
        return FilenameUtils.getFullPath(filePath);
    }

    /**
     * 判断文件扩展名是否匹配
     *
     * @param filePath 文件路径
     * @param extension 扩展名
     * @return 是否匹配
     */
    public static boolean isExtension(String filePath, String extension) {
        return FilenameUtils.isExtension(filePath, extension);
    }

    /**
     * 从URL下载文件
     *
     * @param url URL地址
     * @param filePath 保存路径
     * @throws IOException 下载异常
     */
    public static void downloadFileFromUrl(String url, String filePath) throws IOException {
        FileUtils.copyURLToFile(new URL(url), new File(filePath));
    }

    /**
     * 计算文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return FileUtils.sizeOf(file);
    }

    /**
     * 计算目录大小
     *
     * @param dirPath 目录路径
     * @return 目录大小（字节）
     */
    public static long getDirectorySize(String dirPath) {
        File directory = new File(dirPath);
        return FileUtils.sizeOfDirectory(directory);
    }

    /**
     * 比较两个文件内容是否相同
     *
     * @param file1Path 文件1路径
     * @param file2Path 文件2路径
     * @return 是否相同
     * @throws IOException 比较异常
     */
    public static boolean contentEquals(String file1Path, String file2Path) throws IOException {
        File file1 = new File(file1Path);
        File file2 = new File(file2Path);
        return FileUtils.contentEquals(file1, file2);
    }

    /**
     * 清空目录内容
     *
     * @param dirPath 目录路径
     * @throws IOException 清空异常
     */
    public static void cleanDirectory(String dirPath) throws IOException {
        File directory = new File(dirPath);
        FileUtils.cleanDirectory(directory);
    }


    /******************************************************************************************/


    /**
     * 方法1：使用Apache Commons IO获取目录下所有文件名（不包含子目录）
     *
     * @param directoryPath 目录路径
     * @return 文件名列表
     */
    public static List<String> getFileNamesInDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        // 只获取文件，不包括子目录
        Collection<File> files = FileUtils.listFiles(directory, null, false);
        List<String> fileNames = new ArrayList<>();

        for (File file : files) {
            fileNames.add(file.getName());
        }

        return fileNames;
    }

    /**
     * 方法2：使用Apache Commons IO递归获取所有文件名（包含子目录）
     *
     * @param directoryPath 目录路径
     * @return 文件名列表
     */
    public static List<String> getAllFileNamesRecursive(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        // 递归获取所有文件
        Collection<File> files = FileUtils.listFiles(directory, null, true);
        List<String> fileNames = new ArrayList<>();

        for (File file : files) {
            fileNames.add(file.getName());
        }

        return fileNames;
    }

    /**
     * 方法3：获取完整路径的文件列表（不包含子目录）
     *
     * @param directoryPath 目录路径
     * @return 完整路径列表
     */
    public static List<String> getFilePathsInDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        Collection<File> files = FileUtils.listFiles(directory, null, false);
        List<String> filePaths = new ArrayList<>();

        for (File file : files) {
            filePaths.add(file.getAbsolutePath());
        }

        return filePaths;
    }

    /**
     * 方法3：获取完整路径的文件列表（不包含子目录）
     *
     * @param directoryPath 目录路径
     * @return 完整路径列表
     */
    public static List<String> getFilePathsInDirectoryByTableName(String directoryPath, String tableName) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        Collection<File> files = FileUtils.listFiles(directory, null, false);
        List<String> filePaths = new ArrayList<>();

        for (File file : files) {
            if (file.getName().startsWith(tableName+".") || file.getName().startsWith("dir."+tableName+".")) {
                filePaths.add(file.getAbsolutePath());
            }
        }

        return filePaths;
    }

    /**
     * 方法4：递归获取完整路径的文件列表（包含子目录）
     *
     * @param directoryPath 目录路径
     * @return 完整路径列表
     */
    public static List<String> getAllFilePathsRecursive(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        Collection<File> files = FileUtils.listFiles(directory, null, true);
        List<String> filePaths = new ArrayList<>();

        for (File file : files) {
            filePaths.add(file.getAbsolutePath());
        }

        return filePaths;
    }

    /**
     * 方法5：按文件扩展名过滤获取文件名
     *
     * @param directoryPath 目录路径
     * @param extensions    文件扩展名数组（如：{"txt", "pdf", "jpg"}）
     * @param recursive     是否递归搜索
     * @return 文件名列表
     */
    /*public static List<String> getFileNamesByExtension(String directoryPath,
                                                       String[] extensions,
                                                       boolean recursive) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        // 创建文件过滤器
        IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(
                Arrays.asList(extensions),
                IOCase.INSENSITIVE
        );

        Collection<File> files;
        if (recursive) {
            files = FileUtils.listFiles(directory, fileFilter, TrueFileFilter.INSTANCE);
        } else {
            files = FileUtils.listFiles(directory, fileFilter, null);
        }

        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }

        return fileNames;
    }*/

    /**
     * 方法6：获取文件名和完整路径的映射
     *
     * @param directoryPath 目录路径
     * @param recursive     是否递归搜索
     * @return 文件名到路径的映射
     */
    public static Map<String, String> getFileNamePathMap(String directoryPath, boolean recursive) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return new HashMap<>();
        }

        Collection<File> files;
        if (recursive) {
            files = FileUtils.listFiles(directory, null, true);
        } else {
            files = FileUtils.listFiles(directory, null, false);
        }

        Map<String, String> fileNamePathMap = new HashMap<>();
        for (File file : files) {
            fileNamePathMap.put(file.getName(), file.getAbsolutePath());
        }

        return fileNamePathMap;
    }

    /**
     * 方法7：使用FileFilter自定义过滤条件
     *
     * @param directoryPath 目录路径
     * @param filter        文件过滤器
     * @param recursive     是否递归搜索
     * @return 文件名列表
     */
    public static List<String> getFileNamesWithFilter(String directoryPath,
                                                      IOFileFilter filter,
                                                      boolean recursive) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        Collection<File> files;
        if (recursive) {
            files = FileUtils.listFiles(directory, filter, TrueFileFilter.INSTANCE);
        } else {
            files = FileUtils.listFiles(directory, filter, null);
        }

        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }

        return fileNames;
    }

    /**
     * 方法8：按修改时间排序获取文件名
     *
     * @param directoryPath 目录路径
     * @param recursive     是否递归搜索
     * @param ascending     是否升序排列
     * @return 排序后的文件名列表
     */
    public static List<String> getFileNamesSortedByModifiedTime(String directoryPath,
                                                                boolean recursive,
                                                                boolean ascending) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        Collection<File> files;
        if (recursive) {
            files = FileUtils.listFiles(directory, null, true);
        } else {
            files = FileUtils.listFiles(directory, null, false);
        }

        // 转换为列表并排序
        List<File> fileList = new ArrayList<>(files);
        fileList.sort((f1, f2) -> {
            int result = Long.compare(f1.lastModified(), f2.lastModified());
            return ascending ? result : -result;
        });

        List<String> fileNames = new ArrayList<>();
        for (File file : fileList) {
            fileNames.add(file.getName());
        }

        return fileNames;
    }

    /**
     * 方法9：获取文件详细信息列表
     *
     * @param directoryPath 目录路径
     * @param recursive     是否递归搜索
     * @return 文件信息列表
     */
    public static List<FileInfo> getFileInfos(String directoryPath, boolean recursive) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        Collection<File> files;
        if (recursive) {
            files = FileUtils.listFiles(directory, null, true);
        } else {
            files = FileUtils.listFiles(directory, null, false);
        }

        List<FileInfo> fileInfos = new ArrayList<>();
        for (File file : files) {
            FileInfo info = new FileInfo();
            info.setName(file.getName());
            info.setPath(file.getAbsolutePath());
            info.setSize(file.length());
            info.setModifiedTime(file.lastModified());
            info.setDirectory(file.isDirectory());
            fileInfos.add(info);
        }

        return fileInfos;
    }

    /******************************************************************************************/


    // 使用示例
    /*public static void main(String[] args) {
        try {
            // 写入文件
            writeStringToFile("test.txt", "Hello, World!\nThis is a test file.");

            // 读取文件
            String content = readFileToString("test.txt");
            System.out.println("文件内容：\n" + content);

            // 追加内容
            appendStringToFile("test.txt", "\nAppended content.");

            // 读取所有行
            List<String> lines = readLines("test.txt");
            System.out.println("文件行数：" + lines.size());

            // 逐行读取
            System.out.println("逐行读取：");
            readLinesWithIterator("test.txt");

            // 文件信息
            System.out.println("文件扩展名：" + getFileExtension("test.txt"));
            System.out.println("文件名：" + getFileName("test.txt"));
            System.out.println("基础名：" + getBaseName("test.txt"));
            System.out.println("文件大小：" + getFileSize("test.txt") + " bytes");

            // 创建目录
            createDirectory("test_dir");

            // 复制文件
            copyFile("test.txt", "test_dir/test_copy.txt");

            // 比较文件
            boolean equals = contentEquals("test.txt", "test_dir/test_copy.txt");
            System.out.println("文件内容是否相同：" + equals);

            // 清理
            forceDelete("test_dir");
            forceDelete("test.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static class FileInfo {
        private String name;
        private String path;
        private long size;
        private long modifiedTime;
        private boolean isDirectory;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }

        public long getModifiedTime() { return modifiedTime; }
        public void setModifiedTime(long modifiedTime) { this.modifiedTime = modifiedTime; }

        public boolean isDirectory() { return isDirectory; }
        public void setDirectory(boolean directory) { isDirectory = directory; }

        @Override
        public String toString() {
            return "FileInfo{" +
                    "name='" + name + '\'' +
                    ", path='" + path + '\'' +
                    ", size=" + size +
                    ", modifiedTime=" + modifiedTime +
                    ", isDirectory=" + isDirectory +
                    '}';
        }
    }

}
