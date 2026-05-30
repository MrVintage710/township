package com.mrvintage.township.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
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
            case NINE_SLICE -> {
                if(scaling instanceof BlitSpriteScaling.NineSlice nineSlice) {
                    this.blitNineSlicedSprite(graphics, nineSlice, x, y, width, height);
                }
            }
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
            this.blit(graphics, x, y, width, height);
        } else if (height == this.height) {
            this.innerBlit(graphics, x, y, left, height, this.x, this.y, left, height);
            this.blitTiledSprite(graphics, x + left, y, width - left - right, height, this.x + left, this.y, this.width - left - right, this.height);
            this.innerBlit(graphics, x + width - right, y, right, height, this.x + this.width - right, this.y, right, height);
        } else if (width == this.width) {
            this.innerBlit(graphics, x, y, width, top, this.x, this.y, width, top);
            this.blitTiledSprite(graphics, x, y + top, width, height - top - bottom, this.x, this.y + top, this.width, this.height - top - bottom);
            this.innerBlit(graphics, x, y + height - bottom, width, bottom, this.x, this.y + this.height - bottom, width, bottom);
        } else {
            // X---X---------X---X P1, P2, P3, P4
            // |TL |    T    | TR|
            // X---X---------X---X P5, P6, P7, P8
            // |   |         |   |
            // | L |    M    | R |
            // |   |         |   |
            // X---X---------X---X P9, P10, P11, P12
            // |BL |    B    | BR|
            // X---X---------X---X P13, P14, P15, P16

            int xP1 = this.x;
            int yP1 = this.y;

            int xP2 = this.x + nineSlice.left();
            int yP2 = this.y + nineSlice.top();


            this.blitSprite((TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, blitOffset, left, top);
            this.blitTiledSprite(sprite, x + left, y, blitOffset, width - right - left, top, left, 0, nineSlice.width() - right - left, top, nineSlice.width(), nineSlice.height());
            this.blitSprite((TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - right, 0, x + width - right, y, blitOffset, right, top);
            this.blitSprite((TextureAtlasSprite)sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - bottom, x, y + height - bottom, blitOffset, left, bottom);
            this.blitTiledSprite(sprite, x + left, y + height - bottom, blitOffset, width - right - left, bottom, left, nineSlice.height() - bottom, nineSlice.width() - right - left, bottom, nineSlice.width(), nineSlice.height());
            this.blitSprite(sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - right, nineSlice.height() - bottom, x + width - right, y + height - bottom, blitOffset, right, bottom);
            this.blitTiledSprite(sprite, x, y + top, blitOffset, left, height - bottom - top, 0, top, left, nineSlice.height() - bottom - top, nineSlice.width(), nineSlice.height());
            this.blitTiledSprite(sprite, x + left, y + top, blitOffset, width - right - left, height - bottom - top, left, top, nineSlice.width() - right - left, nineSlice.height() - bottom - top, nineSlice.width(), nineSlice.height());
            this.blitTiledSprite(sprite, x + width - right, y + top, blitOffset, left, height - bottom - top, nineSlice.width() - right, top, right, nineSlice.height() - bottom - top, nineSlice.width(), nineSlice.height());
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
