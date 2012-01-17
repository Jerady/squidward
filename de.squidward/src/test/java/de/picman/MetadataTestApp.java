package de.picman;

import java.io.File;
import java.util.Iterator;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.jpeg.JpegDirectory;

public class MetadataTestApp {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
    	File pic = new File("C:/TEMP/DSC_3571.jpg");
		try {
			Metadata metadata = JpegMetadataReader.readMetadata(pic);
			Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);
			String dateOriginal= exifDirectory.getString(ExifDirectory.TAG_DATETIME_ORIGINAL);
			String dateDatetime= exifDirectory.getString(ExifDirectory.TAG_DATETIME);
			String dateDigitized= exifDirectory.getString(ExifDirectory.TAG_DATETIME_DIGITIZED);
			
			System.out.println(dateOriginal);
			System.out.println(dateDatetime);
			System.out.println(dateDigitized);
			
			Directory jpegDirectory = metadata.getDirectory(JpegDirectory.class);
			
			Iterator<Tag> iter = exifDirectory.getTagIterator();
			while(iter.hasNext()){
				System.out.println(iter.next());
			}
			Iterator<Tag> iter2 = jpegDirectory.getTagIterator();
			while(iter2.hasNext()){
				System.out.println(iter2.next());
			}

			
			
		} catch (JpegProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
