package com.mrvintage.township.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public record BlitSprite(ResourceLocation sprite, int x, int y, int width, int height, int sprite_width, int sprite_height, BlitSpriteScaling scaling) {

    public BlitSprite(ResourceLocation sprite, int width, int height) {
        this(sprite, 0, 0, width, height, width, height, BlitSpriteScaling.DEFAULT);
    }

    public BlitSprite(ResourceLocation sprite, int x, int y, int width, int height, int  sprite_width, int sprite_height) {
        this(sprite, x, y, width, height, sprite_width, sprite_height, BlitSpriteScaling.DEFAULT);
    }

    public void blit(GuiGraphics graphics, int x, int y) {
        this.blit(graphics, x, y, this.width, this.height, this.scaling);
    }

    public void blit(GuiGraphics graphics, int x, int y, BlitSpriteScaling scaling) {
        this.blit(graphics, x, y, this.width, this.height, scaling);
    }

    public void blit(GuiGraphics graphics, int x, int y, int width, int height) {
        this.blit(graphics, x, y, width, height, this.scaling);
    }

    public void blit(GuiGraphics graphics, int x, int y, int width, int height, BlitSpriteScaling scaling) {
        switch (scaling.type()) {
            case STRETCH -> this.innerBlit(graphics, x, y, width, height, this.x, this.y, this.width, this.height);
            case TILE -> this.blitTiledSprite(graphics, x, y, width, height, this.x, this.y, this.width, this.height);
            case NINE_SLICE -> this.blitNineSlicedSprite(graphics, (BlitSpriteScaling.NineSlice) scaling, x, y, width, height);
        }
    }

    public BlitSprite slice(int x, int y, int width, int height) {
        return new BlitSprite(this.sprite, this.x + x, this.y + y, width, height, this.sprite_width, this.sprite_height);
    }

    public BlitSprite slice(int x, int y, int width, int height, BlitSpriteScaling scaling) {
        return new BlitSprite(this.sprite, this.x + x, this.y + y, width, height, this.sprite_width, this.sprite_height, scaling);
    }

    private void innerBlit(GuiGraphics graphics, int x, int y, int width, int height, float u, float v, int uOffset, int vOffset ) {
        graphics.blit(this.sprite, x, y, width, height, u, v, uOffset, vOffset, this.sprite_width, this.sprite_height);
    }

    private void blitNineSlicedSprite(GuiGraphics graphics, BlitSpriteScaling.NineSlice nineSlice, int x, int y, int width, int height) {
        int left = Math.min(nineSlice.left(), width / 2);
        int right = Math.min(nineSlice.right(), width / 2);
        int top = Math.min(nineSlice.top(), height / 2);
        int bottom = Math.min(nineSlice.bottom(), height / 2);
        if (width == this.width && height == this.height) {
            this.innerBlit(graphics, x, y, width, height, this.x, this.y, this.width, this.height);
        } else if (height == this.height) {
            this.innerBlit(graphics, x, y, left, height, this.x, this.y, left, height);
            this.blitTiledSprite(graphics, x + left, y, width - left - right, height, this.x + left, this.y, this.width - left - right, this.height);
            this.innerBlit(graphics, x + width - right, y, right, height, this.x + this.width - right, this.y, right, height);
        } else if (width == this.width) {
            this.innerBlit(graphics, x, y, width, top, this.x, this.y, width, top);
            this.blitTiledSprite(graphics, x, y + top, width, height - top - bottom, this.x, this.y + top, this.width, this.height - top - bottom);
            this.innerBlit(graphics, x, y + height - bottom, width, bottom, this.x, this.y + this.height - bottom, width, bottom);
        } else {
            // These are the useful points that we cant use to construct the 9slice from a texture.
            // These points are in texture space (uv).
            // X1  X2        X3  X4
            // X---X---------X---| Y1
            // |TL |    T    | TR|
            // X---X---------X---- Y2
            // |   |         |   |
            // | L |    M    | R |
            // |   |         |   |
            // X---X---------X---- Y3
            // |BL |    B    | BR|
            // X---X---------X---- Y4

            int X1 = this.x;
            int Y1 = this.y;

            int X2 = this.x + nineSlice.left();
            int Y2 = this.y + nineSlice.top();

            int X3 = this.x + (this.width - nineSlice.right());
            int Y3 = this.y + (this.height - nineSlice.bottom());

            //These are in screen space
            int middleWidth = width - left - right;
            int middleHeight = height - top - bottom;

            //Blit Top Left
            this.innerBlit(graphics, x, y, left, top, X1, Y1, left, top);

            //Blit Top
            this.blitTiledSprite(graphics, x + left, y, middleWidth, top, X2, Y1, (X3 - X2), top);

            //Blit Top Right
            this.innerBlit(graphics, x + (width - right), y, right, top, X3, Y1, right, top);

            //Blit Left
            this.blitTiledSprite(graphics, x, y + top, left, middleHeight, X1, Y2, left, (Y3 - Y2));

            //Blit Center
            this.blitTiledSprite(graphics, x + left, y + top, middleWidth, middleHeight, X2, Y2, (X3 - X2), (Y3 - Y2));

            //Blit Right
            this.blitTiledSprite(graphics, x + (width - right), y + top, right, middleHeight, X3, Y2, right, (Y3 - Y2));

            //Blit Bottom Left
            this.innerBlit(graphics, x, y + (height - bottom), left, bottom, X1, Y3, left, bottom);

            //Blit Bottom
            this.blitTiledSprite(graphics, x + left, y + (height - bottom), middleWidth, bottom, X2, Y3, (X3 - X2), bottom);

            //Blit Bottom Right
            this.innerBlit(graphics, x + (width - right), y + (height - bottom), right, bottom, X3, Y3, right, bottom);
        }

    }

    private void blitTiledSprite(GuiGraphics graphics, int x, int y, int width, int height, int u, int v, int uOffset, int vOffset) {
        if (width > 0 && height > 0) {
            if (uOffset <= 0 || vOffset <= 0) {
                throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + uOffset + "x" + vOffset);
            }

            for(int i = 0; i < width; i += uOffset) {
                int w = Math.min(uOffset, width - i);

                for(int k = 0; k < height; k += vOffset) {
                    int h = Math.min(vOffset, height - k);
                    this.innerBlit(graphics, x + i, y + k, w, h, u, v, w, h);
//                    this.blitSprite(sprite, nineSliceWidth, nineSliceHeight, uPosition, vPosition, x + i, y + k, blitOffset, j, l);
                }
            }
        }

    }
}
