package com.psi.easymanager.module;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "ProductPic".
 */
public class PxProductPic implements java.io.Serializable {

    private Long id;
    private String objectId;
    private String productId;
    private String imagePath;
    private String code;
    private String name;
    private String format;
    private Long size;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public PxProductPic() {
    }

    public PxProductPic(Long id) {
        this.id = id;
    }

    public PxProductPic(Long id, String objectId, String productId, String imagePath, String code, String name, String format, Long size) {
        this.id = id;
        this.objectId = objectId;
        this.productId = productId;
        this.imagePath = imagePath;
        this.code = code;
        this.name = name;
        this.format = format;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
