package cn.wifiedu.ssm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import com.thoughtworks.xstream.core.util.Base64Encoder;

/**
 * 
 * @author lps
 * @Description:图片处理
 * @version V1.0
 *
 */
public class PictureUtil {

	//允许的上传后缀名
	public static String[] allowExtName = {"jpeg","jpg","png","gif"};
	public static String projectPath = null;
	static{
		//项目路径
		projectPath = new File(PictureUtil.class.getResource("/").getPath()).getParentFile().getParent() + "/";
	}
	
	/**
	 * 
	 * @author lps
	 * 
	 * @Description: 传进来base64编码，返回一个字符串,包含相对路径+文件名， 图片
	 * @param base64
	 * @param filePath	相对路径
	 * @return 
	 * @return String 
	 *
	 */
	public static String base64ToImage(String base64, String filePath){
		if (base64 == null) //图像数据为空  
            return null;
		
		//处理数据，提取后缀名
		String[] split = base64.split(",");
		if(split == null) return null;
		String base64Two = split[1];
		String base64One = split[0];
		String extName = base64One.split("/")[1].split(";")[0];
		if(extName == null) return null;
		extName = "jpeg".equals(extName) ? "jpg" : extName;
		
		
		Base64Encoder decoder = new Base64Encoder();
		byte[] b = decoder.decode(base64Two);
		 for(int i=0;i<b.length;++i)
         {
             if(b[i]<0)
             {//调整异常数据
                 b[i]+=256;
             }
         }
		 
         //生成图片
		 String picPath = filePath + "/" + UUID.randomUUID() + "." + extName;//生成的图片相对路径和名字
		 String imgFilePath = projectPath +  picPath;
		 
		 File file = new File(imgFilePath);
		 if(!file.getParentFile().exists()){
			 boolean result = file.getParentFile().mkdirs();
			 if (!result) {
				 return null;
	         }
		 }
		 
         System.out.println(imgFilePath);
         OutputStream out = null;
         try {
			out = new FileOutputStream(imgFilePath);
	        out.write(b);
	        out.flush();
	        out.close();
         } catch (Exception e) {
			 try {
				out.close();
				return null;
			} catch (IOException e1) {
				e1.printStackTrace();
				return null;
			}
		}   
		return picPath;
	}
	
	
	/**
	 * 
	 * @author lps
	 * 
	 * @Description: 根据图片路径删除图片
	 * @param picPath 相对路径 例：assets/img/1.jpg
	 * @return 
	 * @return boolean 
	 *
	 */
	public static boolean deletePic(String picPath){
		picPath = projectPath + picPath;
		File file = new File(picPath);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
	}
	
	
}
