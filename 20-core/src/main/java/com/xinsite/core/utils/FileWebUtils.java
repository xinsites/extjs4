package com.xinsite.core.utils;

import com.xinsite.common.enums.LogEnum;
import com.xinsite.common.exception.AppException;
import com.xinsite.common.uitls.io.FileUtils;
import com.xinsite.common.uitls.lang.StringUtils;
import com.xinsite.core.model.UploadModel;
import com.xinsite.core.utils.log.LogError;
import org.apache.commons.fileupload.FileItem;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.Map;

public class FileWebUtils {

    public static String saveFile(HttpServletRequest request, String AllowUploadFileType, long MaxSize) throws Exception {
        String visualPath = "";

        //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = FileUtils.getUploadFildPath();

        // 转换request，解析出request中的文件
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();  // 获取文件map集合
        // 循环遍历，取出单个文件
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile mf = entity.getValue(); // 获取单个文件
            String filename = mf.getOriginalFilename();  // 获得原始文件名
            if (StringUtils.isEmpty(filename)) continue;
            if (mf.getSize() > MaxSize) {
                throw new Exception("文件上传失败，文件太大");
            }
            String fileExtName = FileUtils.getFileExtName(filename);
            if (AllowUploadFileType.indexOf(fileExtName) == -1) {
                throw new Exception("文件上传失败，文件类型不对");
            }

            try {
                visualPath = FileUtils.getVisualPath(savePath, filename);//得到文件的保存目录
                InputStream in = mf.getInputStream();//获取item中的上传文件的输入流
                FileOutputStream out = new FileOutputStream(savePath + visualPath);//创建一个文件输出流
                byte buffer[] = new byte[1024];//创建一个缓冲区
                //判断输入流中的数据是否已经读完的标识
                int len = 0;
                while ((len = in.read(buffer)) > 0) {
                    //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                    out.write(buffer, 0, len);
                }
                in.close();//关闭输入流
                out.close();//关闭输出流
                //item.delete(); //删除处理文件上传时生成的临时文件
                break;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (StringUtils.isEmpty(visualPath)) {
            throw new Exception("请选择文件");
        }
        return visualPath;
    }

    public static UploadModel savefileupload(HttpServletRequest request, String AllowUploadFileType, long MaxSize) throws Exception {
        UploadModel fileUpload = new UploadModel();

        //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = FileUtils.getUploadFildPath();
        // 转换request，解析出request中的文件
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();  // 获取文件map集合

        //判断提交上来的数据是否是上传表单的数据
        if (fileMap.size() == 0) {
            throw new Exception("请选择文件");
        }

        // 循环遍历，取出单个文件
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile mf = entity.getValue(); // 获取单个文件

            String filename = mf.getOriginalFilename();  // 获得原始文件名
            if (StringUtils.isEmpty(filename)) continue;
            if (mf.getSize() > MaxSize) {
                throw new Exception("文件上传失败，文件太大");
            }
            fileUpload.fileSize = mf.getSize();
            fileUpload.fileName = filename;
            fileUpload.extName = FileUtils.getFileExtName(filename);
            if (AllowUploadFileType.indexOf(fileUpload.extName) == -1) {
                throw new Exception("文件上传失败，文件类型不对");
            }
            InputStream in = mf.getInputStream();//获取item中的上传文件的输入流
            fileUpload.visualPath = FileUtils.getVisualPath(savePath, filename);//得到文件的保存目录
            FileOutputStream out = new FileOutputStream(savePath + fileUpload.visualPath);//创建一个文件输出流
            byte buffer[] = new byte[1024];//创建一个缓冲区
            //判断输入流中的数据是否已经读完的标识
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                out.write(buffer, 0, len);
            }
            in.close();//关闭输入流
            out.close();//关闭输出流
            //item.delete(); //删除处理文件上传时生成的临时文件
            break;
        }

        if (StringUtils.isEmpty(fileUpload.visualPath)) {
            throw new Exception("请选择文件");
        }

        return fileUpload;
    }

    public static Map<String, MultipartFile> getMultipartFiles(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        return multipartRequest.getFileMap();  // 获取文件map集合
    }

    public static MultipartFile getMultipartFile(HttpServletRequest request, String key) {
        Map<String, MultipartFile> fileMap = getMultipartFiles(request);
        if (fileMap.containsKey(key)) return fileMap.get(key);
        return null;
    }

    public static String getFileUploadContent(HttpServletRequest request, String key) {
        StringBuilder contert = new StringBuilder();
        MultipartFile multipartFile = getMultipartFile(request, key);
        if (multipartFile != null) {
            Reader reader = null;
            try {
                reader = new InputStreamReader(multipartFile.getInputStream(), "utf-8");
                BufferedReader br = new BufferedReader(reader);
                String line;
                while ((line = br.readLine()) != null) {
                    contert.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return contert.toString();
    }

    public static UploadModel savefileupload(FileItem item, HttpServletRequest request, String AllowUploadFileType, long MaxSize) throws Exception {
        UploadModel fileUpload = new UploadModel();
        //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = FileUtils.getUploadFildPath();
        File tmpFile = new File(savePath);
        if (!tmpFile.exists()) tmpFile.mkdir();

        if (!item.isFormField()) { //如果fileitem中封装的是上传文件
            String filename = item.getName();
            //System.out.println(filename);
            if (filename == null || filename.trim().equals("")) {
                return fileUpload;
            }
            fileUpload.fileName = filename;
            if (item.get().length > MaxSize) {
                throw new AppException("文件上传失败，文件太大");
            }
            fileUpload.fileSize = item.get().length;
            fileUpload.extName = FileUtils.getFileExtName(filename);
            if (AllowUploadFileType.indexOf(fileUpload.extName) == -1) {
                throw new AppException("文件上传失败，文件类型不对");
            }
            InputStream in = item.getInputStream();//获取item中的上传文件的输入流
            fileUpload.visualPath = FileUtils.getVisualPath(savePath, filename);//得到文件的保存目录
            FileOutputStream out = new FileOutputStream(savePath + fileUpload.visualPath);//创建一个文件输出流
            byte buffer[] = new byte[1024];//创建一个缓冲区
            //判断输入流中的数据是否已经读完的标识
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                out.write(buffer, 0, len);
            }
            in.close();//关闭输入流
            out.close();//关闭输出流
        }
        if (StringUtils.isEmpty(fileUpload.visualPath)) {
            throw new Exception("请选择文件");
        }

        fileUpload.visualPath = "static" + fileUpload.visualPath;
        return fileUpload;
    }

    public static String getBuildFileUrl(String filename, String suffix) throws Exception {
        String savePath = FileUtils.getUploadFildPath("tempfiles"); //获取项目动态绝对路径
        String MapUrl = savePath + filename + "." + suffix;
        File tmpFile = new File(savePath);
        if (!tmpFile.exists()) tmpFile.mkdir();
        return MapUrl;
    }

    public static String fileDownLoad(HttpServletRequest request, HttpServletResponse response, String filepath, String filename, String fileType) throws Exception {
        try {
            File my_file = new File(filepath);
            if (!my_file.exists()) {
                return "文件不存在";
            }
//            String fileType = "application/ms-excel";
            if (!StringUtils.isEmpty(fileType)) response.setContentType(fileType);

            String userAgent = request.getHeader("User-Agent");
            // 针对IE或者以IE为内核的浏览器：
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                filename = java.net.URLEncoder.encode(filename, "UTF-8");
            } else {
                // 非IE浏览器的处理：
                filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
            }
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", filename));
            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(my_file);
            response.setHeader("Content-Length", String.valueOf(in.available()));
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.flush();
            return "";
        } catch (Exception ex) {
            LogError.write("下载文件", LogEnum.Error, ex.toString());
        }
        return "下载文件出错";
    }

    /**
     * 复制单个文件，如果目标文件存在，则不覆盖
     *
     * @param srcFileName  待复制的文件名
     * @param descFileName 目标文件名
     * @return 如果复制成功，则返回true，否则返回false
     */
    public static boolean fileCopyTo(String srcFileName, String descFileName) {
        return FileUtils.copyFileCover(srcFileName, descFileName, true);
    }

    /**
     * 获取文件内容
     */
    public static String readFileToString(String classResourcePath) {
        return FileUtils.readFileToString(classResourcePath);
    }

    /**
     * 获取文件路径
     */
    public static String getResourcePath(String classResourcePath) throws IOException {
        String path = new ClassPathResource(classResourcePath).getURL().getPath();
        return URLDecoder.decode(path, "utf-8");
    }

    /**
     * 获取文件内容
     */
    public static String getMapPath(String buildDir, String filename) {
        return buildDir + filename;
    }

    /**
     * 检查文件是否存在
     */
    public static boolean isFile(String inputFile) {
        File file = new File(inputFile);
        if (!file.isFile() || !file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 创建单个文件
     */
    public static boolean createFile(String descFileName, String content) {
        File file = new File(descFileName);
        if (descFileName.endsWith(File.separator)) {
            return false;
        }
        if (!file.getParentFile().exists()) {
            // 如果文件所在的目录不存在，则创建目录
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        // 创建文件
        try {
            FileUtils.writeToFile(descFileName, StringUtils.unicodeToString(content), false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查文件目录，不存在，创建一个
     */
    public static void checkPath(String savePath) {
        File tmpFile = new File(savePath);
        if (!tmpFile.exists()) tmpFile.mkdir();
    }
}
