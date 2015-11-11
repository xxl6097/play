package org.videolan.libvlc;

public interface IVideoPlayer {
    /**
     * This method is called by native vout to request a surface resize when frame size doesn't match surface size.
     * @param width Frame width
     * @param height Frame height
     * @param visible_width Visible frame width
     * @param visible_height Visible frame height
     * @param sar_num Surface aspect ratio numerator
     * @param sar_den Surface aspect ratio denominator
     */
    void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den);
}
