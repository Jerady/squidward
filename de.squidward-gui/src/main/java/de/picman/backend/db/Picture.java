/*
 * Created on 06.07.2008
 */
package de.picman.backend.db;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;
import javax.media.jai.RenderedOp;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ForwardSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codecimpl.PNGImageEncoder;

import de.picman.backend.control.ApplicationControl;

/** 
 * @author Ole Rahn
 * 
 */
public class Picture extends DatabaseObject implements Comparable<Picture> {

    protected RenderedImage picture, thumbnail, preview;

    protected String description = "", origin = "", creationDate = "";

    protected int userId = -1;

    protected boolean isDeleted = false, exemplary = false,
            publication = false, badExample = false;

    protected Date savingDate = null;

    protected final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    protected static DecimalFormat numberFormat = null;

    public static final String PNGFILEEXTENSION = ".PNG";
    public static final String TIFFFILEEXTENSION = ".TIFF";
    public static final String PICMANFILEEXTENSION = ".picman";

    /**
     * Constructor to be used, when loading images from the database.
     * @param id
     * @param user_id
     * @param description
     * @param creation_date
     * @param saving_date
     * @param origin
     * @param thumbnail
     * @param publication
     * @param exemplary
     * @param bad_example
     * @param isDeleted
     */
    public Picture(int id, int user_id, String description,
            String creation_date, Date saving_date, String origin,
            RenderedImage thumbnail, boolean publication, boolean exemplary,
            boolean bad_example, boolean isDeleted) {
        super(id);
        this.userId = user_id;
        this.description = description;
        this.creationDate = creation_date;
        this.origin = origin;
        this.publication = publication;
        this.exemplary = exemplary;
        this.badExample = bad_example;
        this.isDeleted = isDeleted;
        this.savingDate = saving_date;
        this.preview = null;
        this.setThumbnail( thumbnail );

       //getDbController().logFine(this, "object created from database");
    }

    /**
     * for newly created images, only (not yet saved in DB)
     * @param user_id
     * @param description
     * @param creation_date
     * @param origin
     * @param publication
     * @param exemplary
     * @param bad_example
     */
    public Picture(int user_id, String description, String creation_date,
            String origin, boolean publication, boolean exemplary,
            boolean bad_example) {
        super(-1);
        this.userId = user_id;
        this.description = description;
        this.creationDate = creation_date;
        this.origin = origin;
        this.publication = publication;
        this.exemplary = exemplary;
        this.badExample = bad_example;

        getDbController().logFine(this, "object created from the scratch");
    }

    protected final static DbController getDbController() {
        return ApplicationControl.getInstance().getDbController();
    }

    protected Document getMetadataAsDocument() {
        Element rootElem = new Element(this.getClass().getName());
        Document doc = new Document(rootElem);

        PictureColumn[] columns = PictureColumn.values();

        for (int i = 0; i < columns.length; i++) {
            if (getPropertyAsString(columns[i]) != null)
                rootElem.setAttribute(columns[i].getColumnName(),
                        getPropertyAsString(columns[i]));
        }

        return doc;
    }

    protected String getPropertyAsString(PictureColumn col) {
        switch (col) {
        case CREATIONDATE:
            return getCreationDate();
        case ISBADEXAMPLE:
            return Boolean.toString(isBadExample());
        case DESCRIPTION:
            return getDescription();
        case ISDELETED:
            return Boolean.toString(isDeleted());
        case ISEXEMPLARY:
            return Boolean.toString(isExemplary());
        case ISPUBLICATION:
            return Boolean.toString(isPublication());
        case ORIGIN:
            return getOrigin();
        case SAVINGDATE:
            return dateFormat.format(getSavingDate());
        case ID:
            return Integer.toString(getId());
        case USERID:
            return Integer.toString(getUserId());
        default:
            return null;
        }
    }

    public static DecimalFormat getNumberFormat() {
        if (numberFormat == null) {
            numberFormat = new DecimalFormat("000000");
            numberFormat.setMaximumFractionDigits(0);
            numberFormat.setMinimumFractionDigits(0);
            numberFormat.setMaximumIntegerDigits(10);
            numberFormat.setMinimumIntegerDigits(6);
        }
        return numberFormat;
    }

    /**
     * Export the picture object to one file with meta data plus a full size PNG
     * file in the given base directory.
     * 
     * @param baseDirectory
     *            directory to export the picture to
     * @param baseName
     *            for the picture file name, maybe <code>null</code>
     * @param overwrite
     *            overwrite files with the same name in destination directory
     * @param writeXmlForReimport 
     *              should an XML file containing data for reimport be written?
     * @param dim the dimensions to fit the exported picture in - if null the original size will be used
     * @throws Exception
     */
    public File exportToFile(File baseDirectory, String baseName,
            boolean overwrite, boolean writeXmlForReimport, Dimension dim, SupportedImageFiles type) throws Exception {
        getDbController().logFine(
                this,
                "attempting to export picture to directory "
                        + baseDirectory.getAbsolutePath());
        File result = null;
        if (writeXmlForReimport){
            result = this.writeMetadataFile(baseDirectory, baseName, overwrite);
            this.writePictureFile(baseDirectory, baseName, overwrite, dim, type);
        } else {
            result = this.writePictureFile(baseDirectory, baseName, overwrite, dim, type);
        }
        
        getDbController().logFine(this,
                "successfully exported picture to directory.");
        this.clearPicture();
        System.gc();
        return result;
    }
    
    public File getAsTempFile(Dimension dim) throws Exception {
        getDbController().logFine(
                this,
                "attempting to export picture as TEMP file.");
        File result = null;
        result = this.writePictureTempFile(dim);
        
        
        getDbController().logFine(this,
                "successfully exported picture to TEMP file.");
        return result;
    }
    
    /**
     * Creates a temp file and writes the picture in it. 
     * @param dim max. dimensions of the exported picture, if dim is null the original size will be used for export.
     * @return
     * @throws Exception
     */
    protected File writePictureTempFile(Dimension dim) throws Exception {
        File destination = File.createTempFile("picman_ext_", ".PNG");
       
        RenderedImage image = getPicture();
            
        if (dim!=null){
            image = getScaledCopy(image, dim);
        }
        FileOutputStream pngOut = new FileOutputStream(destination);

        /* save png image: */
        PNGEncodeParam param = PNGEncodeParam.getDefaultEncodeParam(image);
        PNGImageEncoder encoder = (PNGImageEncoder) ImageCodec.createImageEncoder("png", pngOut, param);
        encoder.encode(image);
        pngOut.close();
        
        destination.deleteOnExit();
        
        return destination;
    }
    
    protected final static File createDbUploadFile(RenderedImage image)
            throws IOException {

        File tmpImage = File.createTempFile("picman", PNGFILEEXTENSION);
        tmpImage.deleteOnExit();

        FileOutputStream pngOut = new FileOutputStream(tmpImage);

        /* save png image: */
        PNGEncodeParam param = PNGEncodeParam.getDefaultEncodeParam(image);
        PNGImageEncoder encoder = (PNGImageEncoder) ImageCodec
                .createImageEncoder("png", pngOut, param);
        encoder.encode(image);
        pngOut.close();

        return tmpImage;
    }

    /**
     * Imports pictures including all meta data, that were exported from PICMAN, e.g. with
     * {@link DbController#exportPicture(Picture, File, String, boolean)}
     * @param file meta data or picture file that was exported from a PICMAN installation
     * @param reuseIds if <code>true</code> User & Picture ID from meta data file will be used - only use if you want to restore, 
     * for all other purposes, set this to <code>false</code>!!
     * @return {@link Picture} object - not yet saved to database!
     * @throws JDOMException
     * @throws IOException
     * @throws ParseException
     */
    public static Picture importPictureFromFile(File file, boolean reuseIds) throws JDOMException, IOException, ParseException {
        if (ApplicationControl.getInstance().getCurrentUser().isReadonly()){
            throw new IllegalStateException("Can not perform Insert,Update,Delete - you are a read-only user!");
        }
        File metadata = getMetadataFile(file);
        File picture = getPictureFile(file);

        if (!metadata.exists())
            throw new IllegalArgumentException("Meta data file does not exist!");
        if (!picture.exists())
            throw new IllegalArgumentException("Image file does not exist!");
        
        SAXBuilder sxbuild = new SAXBuilder();
        InputSource is = new InputSource(metadata.getAbsolutePath());
        Document doc = sxbuild.build(is);
        Element root = doc.getRootElement();
        
        Picture newPic = null;
        
        if (!reuseIds){
            newPic = new Picture(ApplicationControl.getInstance().getCurrentUser().getId(),
                                        root.getAttribute(PictureColumn.DESCRIPTION.getColumnName()).getValue(),
                                        root.getAttribute(PictureColumn.CREATIONDATE.getColumnName()).getValue(),
                                        root.getAttribute(PictureColumn.ORIGIN.getColumnName()).getValue(),
                                        root.getAttribute(PictureColumn.ISPUBLICATION.getColumnName()).getBooleanValue(),
                                        root.getAttribute(PictureColumn.ISEXEMPLARY.getColumnName()).getBooleanValue(),
                                        root.getAttribute(PictureColumn.ISBADEXAMPLE.getColumnName()).getBooleanValue()
                                        );
        } else {
            /*
            int id, 
            int user_id, 
            String description,
            String creation_date, 
            Date saving_date, 
            String origin,
            RenderedImage thumbnail, 
            boolean publication, 
            boolean exemplary,
            boolean bad_example, 
            boolean isDeleted 
            */

            newPic = new Picture(root.getAttribute(PictureColumn.ID.getColumnName()).getIntValue(),
                    root.getAttribute(PictureColumn.USERID.getColumnName()).getIntValue(),
                    root.getAttribute(PictureColumn.DESCRIPTION.getColumnName()).getValue(),
                    root.getAttribute(PictureColumn.CREATIONDATE.getColumnName()).getValue(),
                    new Date(dateFormat.parse(root.getAttribute(PictureColumn.SAVINGDATE.getColumnName()).getValue()).getTime()),
                    root.getAttribute(PictureColumn.ORIGIN.getColumnName()).getValue(),
                    (RenderedImage)null,
                    root.getAttribute(PictureColumn.ISPUBLICATION.getColumnName()).getBooleanValue(),
                    root.getAttribute(PictureColumn.ISEXEMPLARY.getColumnName()).getBooleanValue(),
                    root.getAttribute(PictureColumn.ISBADEXAMPLE.getColumnName()).getBooleanValue(),
                    root.getAttribute(PictureColumn.ISDELETED.getColumnName()).getBooleanValue()
                    );
        }
        newPic.setPicture(loadImageFromFile(picture));
        try {
            newPic.setThumbnail(getScaledCopy(newPic.getPicture(), ApplicationControl.getInstance().getPictureThumbnailDimension()));
        } catch (Exception e){
            ApplicationControl.getInstance().logWarn(newPic, "Error while setting Thumbnail: " + e.toString());
        }
                                        
        return newPic;
    }

    protected static File getMetadataFile(File file) {
        String path = file.getAbsolutePath().toLowerCase();
        if (path.endsWith(PICMANFILEEXTENSION.toLowerCase())){
            return file;
        }
        return new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(PNGFILEEXTENSION)));
    }

    protected static File getPictureFile(File file) {
        String path = file.getAbsolutePath().toLowerCase();
        if (path.endsWith(PICMANFILEEXTENSION.toLowerCase()+PNGFILEEXTENSION.toLowerCase())){
            return file;
        }
        return new File(file.getAbsolutePath()+PNGFILEEXTENSION);
    }

    protected String generateFileName(String baseName) {
        String desc = getDescription();

        if (baseName == null || baseName.length() < 15) {
            desc = desc.replaceAll("[ .#/\\\"'!§$%&=?,;:~*+@€]", "_");
            desc = desc.substring(0, Math.min(desc.length(), 20));
        } else {
            desc = "";
        }
        return (baseName != null ? baseName + "_" : "")
                + getNumberFormat().format(getId())
                + (desc.length() > 0 ? "_" + desc : "") + PICMANFILEEXTENSION;
    }

    protected File writeMetadataFile(File baseDirectory, String baseName,
            boolean overwrite) throws Exception {
        if (!baseDirectory.isDirectory())
            throw new IllegalArgumentException(
                    "given File object is not a directory");
        else if (!baseDirectory.canWrite())
            throw new IllegalArgumentException(
                    "given directory is not writeable");

        String fileName = baseDirectory.getAbsolutePath() + File.separator
                + generateFileName(baseName);
        File destination = new File(fileName);
        if (!destination.exists() || overwrite) {
            Document doc = getMetadataAsDocument();
            XMLOutputter outputter = new XMLOutputter();
            FileOutputStream output = new FileOutputStream(fileName);
            outputter.output(doc, output);
            return destination;
        } else {
            ApplicationControl
                    .getInstance()
                    .logWarn(
                            "destination of file export already exists and is not supposed to be overwritten: "
                                    + destination.getAbsolutePath());
        }
        return null;
    }
    
    
    protected File writePictureFile(File baseDirectory, String baseName,
            boolean overwrite, Dimension dim, SupportedImageFiles type) throws Exception {
        if (!baseDirectory.isDirectory())
            throw new IllegalArgumentException(
                    "given File object is not a directory");
        else if (!baseDirectory.canWrite())
            throw new IllegalArgumentException(
                    "given directory is not writeable");

        String fileName = baseDirectory.getAbsolutePath() + File.separator
                + generateFileName(baseName);
        
        if (type.equals(SupportedImageFiles.PNG)){
            fileName += PNGFILEEXTENSION;
        } else {
            fileName += TIFFFILEEXTENSION;
        }
        File destination = new File(fileName);
        if (!destination.exists() || overwrite) {
            RenderedImage image = getPicture();
            RenderedOp imgFile = null;
            
            if (dim!=null){
                image = getScaledCopy(image, dim);
            }
            
            if (type.equals(SupportedImageFiles.PNG)){
                imgFile = JAI.create("filestore", image, fileName, "PNG");
            } else {
                imgFile = JAI.create("filestore", image, fileName, "TIFF");
            }
            if (imgFile!=null){
                imgFile.dispose();
                imgFile = null;
            }
            Runtime.getRuntime().gc();
        } else {
            ApplicationControl
                    .getInstance()
                    .logWarn(
                            "destination of file export already exists and is not supposed to be overwritten: "
                                    + destination.getAbsolutePath());
        }
        return destination;
    }

    /**
     * Fills the actual full-size picture from the database into the given
     * <code>Picture</code> object.
     * 
     * @param toBeFilled
     * @throws Exception
     */
    protected final static void fillInFullSizeImage(Picture toBeFilled)
            throws Exception {
        String sql = "SELECT id, picture FROM pictures WHERE id = "
                + toBeFilled.getId() + ";";
        getDbController().logFine(toBeFilled,
                "attempting to load full size image, SQL: " + sql);
        ResultSet result = getDbController().doSelect(sql);
        if (!result.next()) {
            getDbController().logWarn(toBeFilled, "no full size image found");
            throw new IllegalArgumentException("Kein Bild mit der ID "
                    + toBeFilled.getId() + " in der Datenbank gefunden.");
        }
        RenderedImage image = loadImageFromStream(result
                .getBinaryStream("picture"));

        if (image == null) {
            getDbController().logSevere(toBeFilled,
                    "no picture data found in the database: " + image);
            throw new IllegalStateException(
                    "no picture data found in the database");
        }

        toBeFilled.setPicture(image);
        getDbController().logFine(toBeFilled,
                "successfully loaded full size image: " + image);
    }

    /**
     * Fills the preview picture from the database into the given
     * <code>Picture</code> object.
     * 
     * @param toBeFilled
     * @throws Exception
     */
    public final static void fillInPreviewImage(Picture toBeFilled)
            throws Exception {
        String sql = "select id, preview from "
                + DbConstants.TABLE_NAME_PICTURES + " where "
                + DbConstants.FIELD_ID + " = " + toBeFilled.getId() + ";";
        getDbController().logFine(toBeFilled,
                "attempting to load preview image, SQL: " + sql);
        ResultSet result = getDbController().doSelect(sql);
        if (!result.next()) {
            getDbController().logWarn(toBeFilled, "no preview image found");
            throw new IllegalArgumentException("Kein Bild mit der ID "
                    + toBeFilled.getId() + " in der Datenbank gefunden.");
        }
        RenderedImage image = loadImageFromStream(result
                .getBinaryStream("preview"));

        if (image == null) {
            getDbController().logSevere(toBeFilled,
                    "no preview picture data found in the database: " + image);
            throw new IllegalStateException(
                    "no preview picture data found in the database");
        }

        toBeFilled.setPreview(image);
        getDbController().logFine(toBeFilled,
                "successfully loaded preview image: " + image);
    }
    
    /**
     * Fills the thumbnail picture from the database into the given
     * <code>Picture</code> object.
     * 
     * @param toBeFilled
     * @throws Exception
     */
    public final static void fillInThumbnailImage(Picture toBeFilled)
            throws Exception {
        String sql = "select id, thumbnail from "
                + DbConstants.TABLE_NAME_PICTURES + " where "
                + DbConstants.FIELD_ID + " = " + toBeFilled.getId() + ";";
        getDbController().logFine(toBeFilled,
                "attempting to load thumbnail image, SQL: " + sql);
        ResultSet result = getDbController().doSelect(sql);
        if (!result.next()) {
            getDbController().logWarn(toBeFilled, "no thumbnail image found");
            throw new IllegalArgumentException("Kein Bild mit der ID "
                    + toBeFilled.getId() + " in der Datenbank gefunden.");
        }
        RenderedImage image = loadImageFromStream(result
                .getBinaryStream("thumbnail"));

        if (image == null) {
            getDbController().logSevere(toBeFilled,
                    "no thumbnail picture data found in the database: " + image);
            throw new IllegalStateException(
                    "no thumbnail picture data found in the database");
        }

        toBeFilled.setThumbnail(image);
        getDbController().logFine(toBeFilled,
                "successfully loaded thumbnail image: " + image);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setCreationDate(String creation_date) {
        this.creationDate = creation_date;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setExemplary(boolean exemplary) {
        this.exemplary = exemplary;
    }

    public void setPublication(boolean publication) {
        this.publication = publication;
    }

    public void setBadExample(boolean bad_example) {
        this.badExample = bad_example;
    }

    protected void setId(int id) {
        this.id = id;
    }

    /**
     * @return true if the full size image is present in RAM, false if not if
     *         e.g. it needs to be loaded from the database.
     */
    public boolean isFullSizeImagePresent() {
        return this.picture != null;
    }

    /**
     * @return true if the preview image is present in RAM, false if not if e.g.
     *         it needs to be loaded from the database.
     */
    public boolean isPreviewImagePresent() {
        return this.preview != null;
    }

    protected static RenderedImage getScaledCopy(RenderedImage image,
            Dimension maxDim) {
        return getScaledCopy(image, maxDim.width, maxDim.height);
    }

    /**
     * 
     * @param image
     * @param maxImgWidth
     *            used to scale the image to a maximum size
     * @param maxImgHeight
     *            not used, yet
     * @return a scaled version of the given image
     */
    protected static RenderedImage getScaledCopy(RenderedImage image,
            int maxImgWidth, int maxImgHeight) {
        // RenderedImage image = JAI.create("stream", stream);

        float width = image.getWidth();
        float height = image.getHeight();

        float ratio = (width / height) * (4 / 3);

        float maxWidth = 1f;
        float maxHeight = 1f;

        if (ratio >= 1) {
            // width is too big for a 4-to-3 ratio
            maxWidth = maxImgWidth;
            maxHeight = height * maxImgWidth / width;
        } else {
            // height is too big for a 4-to-3 ratio
            maxHeight = maxImgHeight;
            maxWidth = width * maxImgHeight / height;
        }

        ParameterBlock params = new ParameterBlock();
        params.addSource(image);
        params.add(maxWidth / width); // x scale factor
        params.add(maxHeight / height); // y scale factor
        params.add(0.0F); // x translate
        params.add(0.0F); // y translate
        params.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2)); // interpolation
        // method
        /* Create an operator to scale image1. */
        RenderedOp image2 = JAI.create("scale", params);

        return image2.createSnapshot();
    }

    public RenderedImage getScaledPreview(int maxImgWidth, int maxImgHeight)
            throws Exception {
        return Picture.getScaledCopy(this.getPreview(), maxImgWidth,
                maxImgHeight);
    }

    public RenderedImage getScaledThumbnail(int maxImgWidth, int maxImgHeight)
            throws Exception {
        return Picture.getScaledCopy(this.getThumbnail(), maxImgWidth,
                maxImgHeight);
    }

    public BufferedImage getScaledPreviewAsBufferedImage(int maxImgWidth,
            int maxImgHeight) throws Exception {
        return PlanarImage.wrapRenderedImage(
                this.getScaledPreview(maxImgWidth, maxImgHeight))
                .getAsBufferedImage();
    }

    public BufferedImage getScaledThumbnailAsBufferedImage(int maxImgWidth,
            int maxImgHeight) throws Exception {
        return PlanarImage.wrapRenderedImage(
                this.getScaledThumbnail(maxImgWidth, maxImgHeight))
                .getAsBufferedImage();
    }

    /**
     * Generates thumbnails from image files, e.g. for previews in a file
     * browser.
     * 
     * @param imageFile
     *            file object of the image to get a thumbnail from.
     * @param maxDim
     * @param samplingFactor
     *            the higher this number, the faster the file loading, but also
     *            the worse the thumbnail quality. 1 means the all pixels of the
     *            image will considered for the thumbnail. E.g. 5 means every
     *            5th pixel (x as well as y) will be considered. 10 is very
     *            fast, even for 10MB+ pictures, but looks very bad in small
     *            images, maybe there should be 3 different steps, depending in
     *            the (estimated) image size, e.g.<br>
     *            <ul>
     *            <li> 3 for pictures with a width of less than 300px</li>
     *            <li> 7 for pictures with a width of less than 1000px</li>
     *            <li>10 for pictures with a width of more than 1000px</li>
     *            </ul>
     * @return thumbail scaled to the given dimension.
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
	public final static BufferedImage readThumbnailFromFile(File imageFile,
            Dimension maxDim, int samplingFactor) throws Exception {
        Iterator readers = ImageIO.getImageReadersBySuffix(imageFile.getName()
                .substring(imageFile.getName().lastIndexOf('.') + 1));
        ImageReader reader = (ImageReader) readers.next();

        ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
        reader.setInput(iis, true);

        BufferedImage bi = null;

        if (reader.getNumThumbnails(0) > 0 && reader.readerSupportsThumbnails()) {
            int thumbnailIndex = 0;
            bi = reader.readThumbnail(0, thumbnailIndex);

            float width = bi.getWidth();
            float height = bi.getHeight();

            float ratio = (width / height) * (4 / 3);

            float maxWidth = 1f;
            float maxHeight = 1f;

            if (ratio >= 1) {
                // width is too big for a 4-to-3 ratio
                maxWidth = maxDim.width;
                maxHeight = height * maxDim.width / width;
            } else {
                // height is too big for a 4-to-3 ratio
                maxHeight = maxDim.height;
                maxWidth = width * maxDim.height / height;
            }

            bi.getScaledInstance((int) maxWidth, (int) maxHeight,
                    Image.SCALE_FAST);

        } else {
            ImageReadParam param = reader.getDefaultReadParam();
            param = reader.getDefaultReadParam();
            param.setSourceSubsampling(samplingFactor, samplingFactor, 0, 0);
            RenderedImage img = reader.readAsRenderedImage(0, param);
            bi = RenderedImageAdapter.wrapRenderedImage(
                    getScaledCopy(img, maxDim)).getAsBufferedImage();
        }

        return bi;
    }

    /**
     * free memory by setting the full size image and preview to
     * <code>null</code>.
     */
    public void clearPicture() {
        this.picture = null;
        this.preview = null;
    }

    /**
     * implicitly loads the full size image if neccessary and if available
     * 
     * @return the full size image
     * @throws Exception
     */
    public RenderedImage getPicture() throws Exception {
        getDbController().logFine(this, "providing full size image");
        if (picture == null && getId() > -1) {
            getDbController().logFine(this,
                    "data member empty, loading full size image from database");
            // implicit image load
            Picture.fillInFullSizeImage(this);
        }
        return picture;
    }

    public BufferedImage getPictureAsBufferedImage() throws Exception {
        return PlanarImage.wrapRenderedImage(getPicture()).getAsBufferedImage();
    }

    /**
     * sets the reference to the full size image NULL in order to free RAM.
     */
    public void setPictureNull() {
        this.picture = null;
    }

    public void cacheThumbnail(){
        if (getDbController().isLocalCaching()){
            File localFile = getDbController().getThumnailCacheFile(this);
            
            if (thumbnail!=null && !localFile.exists()){
                RenderedOp imgFile = null;
                SupportedImageFiles type = SupportedImageFiles.PNG;
                
                try {
                    if (type.equals(SupportedImageFiles.PNG)){
                        imgFile = JAI.create("filestore", thumbnail, localFile.getAbsolutePath(), "PNG");
                    } else {
                        imgFile = JAI.create("filestore", thumbnail, localFile.getAbsolutePath(), "TIFF");
                    }
                    if (imgFile!=null){
                        imgFile.dispose();
                        imgFile = null;
                    }
                    Runtime.getRuntime().gc();
                } catch (Exception e){
                    getDbController().logWarn(this, "Can not create local chache file: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    public RenderedImage getThumbnail() throws Exception {
        if (thumbnail==null && getId()>0){
            File localFile = getDbController().getThumnailCacheFile(this);
            
            if (localFile.exists() && localFile.canRead()){
                try {
                    thumbnail = ImageIO.read(localFile);
                } catch (IOException e) {
                    getDbController().logWarn(this,"Can not access local chache file: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (thumbnail==null){
                // if still null - (re-) load from DB
                Picture.fillInThumbnailImage(this);
                this.cacheThumbnail();
            }
        }
        return thumbnail;
    }

    public BufferedImage getThumbnailAsBufferedImage() throws Exception {
        return PlanarImage.wrapRenderedImage(getThumbnail())
                .getAsBufferedImage();
    }

    public RenderedImage getPreview() throws Exception {
        getDbController().logFine(this, "providing preview image");
        if (preview == null && getId() > -1) {
            getDbController().logFine(this,
                    "data member empty, loading preview image from database");
            // implicit image load
            Picture.fillInPreviewImage(this);
        }
        return preview;
    }

    public BufferedImage getPreviewAsBufferedImage() throws Exception {
        return PlanarImage.wrapRenderedImage(getPreview()).getAsBufferedImage();
    }

    public String getDescription() {
        return description;
    }

    public String getOrigin() {
        return origin;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public boolean isExemplary() {
        return exemplary;
    }

    public boolean isPublication() {
        return publication;
    }

    public boolean isBadExample() {
        return badExample;
    }

    public Date getSavingDate() {
        return savingDate;
    }

    public void setPicture(RenderedImage picture) {
        this.picture = picture;
    }

    protected void setThumbnail(RenderedImage thumbnail) {
        this.thumbnail = thumbnail;
        if (getId()>0 && thumbnail!=null){
            this.cacheThumbnail();
        }
    }

    protected void setPreview(RenderedImage preview) {
        this.preview = preview;
    }

    public final static RenderedImage loadImageFromStream(InputStream is) {
        if (is == null)
            return null;
        try {
            ForwardSeekableStream stream = new ForwardSeekableStream(is);
            RenderedImage image = JAI.create("stream", stream);
            return image;
        } catch (Exception e) {
            // couldn't read image.
            ApplicationControl.displayErrorToUser(e);
        }
        return null;
    }

    public final static RenderedImage loadImageFromFile(File imgFile)
            throws IOException {
        FileSeekableStream fss = new FileSeekableStream(imgFile);
        return loadImageFromStream(fss);
    }

    public int compareTo(Picture o) {
        return getDescription().compareTo(o.getDescription());
    }

}
