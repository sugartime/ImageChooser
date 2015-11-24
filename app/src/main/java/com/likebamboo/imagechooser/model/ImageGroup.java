/**
 * ImageGroup.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.likebamboo.imagechooser.model;

import java.util.ArrayList;

/**
  *각 항목에 대한 GridView에 데이터 모델
 * 
 * @author likebamboo
 */
public class ImageGroup extends BaseModel {
    /**
     * 폴더 이름
     */
    private String dirName = "";

    /**
     * 폴더에 들어있는 사진들
     */
    private ArrayList<String> images = new ArrayList<String>();

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    /**
     * (커버 등) 경로의 첫 번째 그림
     * 
     * @return
     */
    public String getFirstImgPath() {
        if (images.size() > 0) {
            return images.get(0);
        }
        return "";
    }

    /**
     * 사진 번호를 가져옵니다
     * 
     * @return
     */
    public int getImageCount() {
        return images.size();
    }

    public ArrayList<String> getImages() {
        return images;
    }

    /**
     * 사진추가
     * 
     * @param image
     */
    public void addImage(String image) {
        if (images == null) {
            images = new ArrayList<String>();
        }
        images.add(image);
    }

    @Override
    public String toString() {
        return "ImageGroup [firstImgPath=" + getFirstImgPath() + ", dirName=" + dirName
                + ", imageCount=" + getImageCount() + "]";
    }

    /**
     * <p>
     * 이 메소드를 오버라이드 (override)
     * <p>
     * 픽처 (DIRNAME)는 사진과 함께 동일한 그룹에 속하는 폴더의 파일명 것이면
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ImageGroup)) {
            return false;
        }
        return dirName.equals(((ImageGroup)o).dirName);
    }
}
