package utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OSSUtils {
	private static final transient Logger logger = LoggerFactory.getLogger(OSSUtils.class);
	// 登陆阿里云需要的凭证
	private static final String ACCESS_ID = "";
	private static final String ACCESS_KEY = "";
	// 阿里云对应的地址
	private static final String OSS_ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
	private static final String bucket = "";
	
	public static final  String HOST = "";
	public static final OSSClient client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);
	
	public static final String filedir = "temp/";
	/**
	   * 上传到OSS服务器  如果同名文件会覆盖服务器上的
	   *
	   * @param instream 文件流
	   * @param fileName 文件名称 包括后缀名
	   * @return 返回文件url
	   */
	public static String uploadFile2OssForTemp(InputStream instream, String fileName) {
		String ret = "";
		try {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(instream.available());
			objectMetadata.setCacheControl("no-cache");
			objectMetadata.setHeader("Pragma", "no-cache");
			objectMetadata.setContentDisposition("inline;filename=" + fileName);

			// 上传文件
			client.putObject(bucket, filedir + fileName, instream, objectMetadata);
			URL url = client.generatePresignedUrl(bucket, filedir + fileName, DateUtils.addDays(new Date(), 100));
			ret = url.getProtocol() + "://" + url.getHost() + url.getPath();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("向OSS上传文件失败！", e);
		} finally {
			try {
				if (instream != null) {
					instream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
     * 导出文件命名规则
     * @param prefix
     * @param date
     * @return
     */
    public static String getFileName(String prefix,Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmm");

        if (date == null) {
            Long cur = Calendar.getInstance().getTimeInMillis();
            return "ExportOrder_" + prefix+"_" + timeFormat.format(new Date(cur)) + ".xls";
        } else
            return "ExportOrder_" + prefix+"_" + timeFormat.format(date) + ".xls";
    }
    
	// 获取图片的url ，这里你要传入 路径以及 对应的key
	public static URL getUrlFromTempDir(String fileName) {
		Date expiration_100Days = new Date(new Date().getTime() + 100);
		URL url = client.generatePresignedUrl(bucket, filedir + fileName, expiration_100Days);

		String urlTsr = url.toString();
		URL urlStr = null;
		try {
			urlStr = new URL(urlTsr.split("[?]")[0]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// GeneratePresignedUrlRequest generatePresignedUrlRequest = new
		// GeneratePresignedUrlRequest(bucketName, key);
		// URL generatePresignedUrl =
		// client.generatePresignedUrl(generatePresignedUrlRequest);
		return urlStr;
	}
}
