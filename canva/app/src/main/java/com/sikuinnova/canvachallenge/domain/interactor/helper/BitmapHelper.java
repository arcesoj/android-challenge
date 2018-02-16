package com.sikuinnova.canvachallenge.domain.interactor.helper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import com.google.common.base.Preconditions;
import com.sikuinnova.canvachallenge.data.entity.TileListEntity;
import com.sikuinnova.canvachallenge.presentation.tiles.TileModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by josearce on 7/3/17.
 */
public class BitmapHelper {

    private final Bitmap originalBitmap;
    private final int chunkSideLength;

    public BitmapHelper(int chunkSideLength, Bitmap originalBitmap) {
        Preconditions.checkNotNull(originalBitmap);
        this.originalBitmap = originalBitmap;
        this.chunkSideLength = chunkSideLength;
    }

    public List<TileListEntity> splitBitmapInTilesRow(boolean dynamicTile) {
        if (dynamicTile) {
            return splitBitmapInExactTile();
        } else {
            return splitBitmapByAverage();
        }
    }

    private List<TileListEntity> splitBitmapInExactTile() {
        List<Bitmap> bitmapList = splitBitmap();
        List<TileModel> tileList = new ArrayList<>();
        int size = bitmapList.size();

        for (int i = 0; i < size; i++) {
            Bitmap tileBitmap = bitmapList.get(i);
            TileModel tileModel = new TileModel(tileBitmap.getWidth(), tileBitmap.getHeight(), getAverage(tileBitmap));
            tileList.add(tileModel);
        }

        int cols = originalBitmap.getWidth() / chunkSideLength;
        return chopped(tileList, cols);
    }

    private List<TileListEntity> splitBitmapByAverage() {
        final int rows = originalBitmap.getHeight() / chunkSideLength;
        final int cols = originalBitmap.getWidth() / chunkSideLength;

        // To store all the small image chunks in originalBitmap format in this list
        List<TileModel> chunkedImage = new ArrayList<>(rows * cols);

        // picture perfectly splits into square chunks
        int yCoord = 0;
        for (int y = 0; y < rows; ++y) {
            int xCoord = 0;
            for (int x = 0; x < cols; ++x) {
                chunkedImage.add(getTileModel(Bitmap.createBitmap(originalBitmap, xCoord, yCoord, chunkSideLength, chunkSideLength)));
                xCoord += chunkSideLength;
            }
            yCoord += chunkSideLength;
        }

        return chopped(chunkedImage, cols);
    }

    private TileModel getTileModel(Bitmap bitmap) {
        return new TileModel(chunkSideLength, chunkSideLength, getAverage(bitmap));
    }

    private List<Bitmap> splitBitmap() {
        // height and weight of higher|wider chunks if they would be
        int higherChunkSide;
        int widerChunkSide;

        final int rows = originalBitmap.getHeight() / chunkSideLength;
        higherChunkSide = originalBitmap.getHeight() % chunkSideLength + chunkSideLength;

        final int cols = originalBitmap.getWidth() / chunkSideLength;
        widerChunkSide = originalBitmap.getWidth() % chunkSideLength + chunkSideLength;

        // To store all the small image chunks in originalBitmap format in this list
        List<Bitmap> chunkedImage = new ArrayList<>(rows * cols);

        if (higherChunkSide != chunkSideLength) {
            if (widerChunkSide != chunkSideLength) {
                // picture has both higher and wider chunks plus one big square chunk

                List<Bitmap> widerChunks = new ArrayList<>(rows - 1);
                List<Bitmap> higherChunks = new ArrayList<>(cols - 1);
                Bitmap squareChunk;

                int yCoord = 0;
                for (int y = 0; y < rows - 1; ++y) {
                    int xCoord = 0;
                    for (int x = 0; x < cols - 1; ++x) {
                        chunkedImage.add(Bitmap.createBitmap(originalBitmap, xCoord, yCoord, chunkSideLength, chunkSideLength));
                        xCoord += chunkSideLength;
                    }
                    // add last chunk in a row to array of wider chunks
                    widerChunks.add(Bitmap.createBitmap(originalBitmap, xCoord, yCoord, widerChunkSide, chunkSideLength));

                    yCoord += chunkSideLength;
                }


                // add last row to array of higher chunks
                int xCoord = 0;
                for (int x = 0; x < cols - 1; ++x) {
                    higherChunks.add(Bitmap.createBitmap(originalBitmap, xCoord, yCoord, chunkSideLength, higherChunkSide));
                    xCoord += chunkSideLength;
                }

                //save bottom-right big square chunk
                squareChunk = Bitmap.createBitmap(originalBitmap, xCoord, yCoord, widerChunkSide, higherChunkSide);

                //determine random position of big square chunk
                int bigChunkX = cols - 1;
                int bigChunkY = rows - 1;

                //add wider and higher chunks into resulting array of chunks
                //all wider(higher) chunks should be in one column(row) to avoid collisions between chunks
                //We must insert it row by row because they will displace each other from their columns otherwise
                for (int y = 0; y < rows - 1; ++y) {
                    chunkedImage.add(cols * y + bigChunkX, widerChunks.get(y));
                }

                //And then we insert the whole row of higher chunks
                for (int x = 0; x < cols - 1; ++x) {
                    chunkedImage.add(bigChunkY * cols + x, higherChunks.get(x));
                }

                chunkedImage.add(bigChunkY * cols + bigChunkX, squareChunk);

            } else {
                // picture has only number of higher chunks

                List<Bitmap> higherChunks = new ArrayList<>(cols);

                int yCoord = 0;
                for (int y = 0; y < rows - 1; ++y) {
                    int xCoord = 0;
                    for (int x = 0; x < cols; ++x) {
                        chunkedImage.add(Bitmap.createBitmap(originalBitmap, xCoord, yCoord, chunkSideLength, chunkSideLength));
                        xCoord += chunkSideLength;
                    }
                    yCoord += chunkSideLength;
                }

                // add last row to array of higher chunks
                int xCoord = 0;
                for (int x = 0; x < cols; ++x) {
                    higherChunks.add(Bitmap.createBitmap(originalBitmap, xCoord, yCoord, chunkSideLength, higherChunkSide));
                    xCoord += chunkSideLength;
                }

                //add higher chunks into resulting array of chunks
                //Each higher chunk should be in his own column to preserve original image size
                //We must insert it row by row because they will displace each other from their columns otherwise
                List<Point> higherChunksPositions = new ArrayList<>(cols);
                for (int x = 0; x < cols; ++x) {
                    higherChunksPositions.add(new Point(x, rows - 1));
                }

                //sort positions of higher chunks. THe upper-left elements should be first
                Collections.sort(higherChunksPositions, new Comparator<Point>() {
                    @Override
                    public int compare(Point lhs, Point rhs) {
                        if (lhs.y != rhs.y) {
                            return lhs.y < rhs.y ? -1 : 1;
                        } else if (lhs.x != rhs.x) {
                            return lhs.x < rhs.x ? -1 : 1;
                        }
                        return 0;
                    }
                });

                for (int x = 0; x < cols; ++x) {
                    Point currentCoord = higherChunksPositions.get(x);
                    chunkedImage.add(currentCoord.y * cols + currentCoord.x, higherChunks.get(x));
                }

            }
        } else {
            if (widerChunkSide != chunkSideLength) {
                // picture has only number of wider chunks

                List<Bitmap> widerChunks = new ArrayList<>(rows);

                int yCoord = 0;
                for (int y = 0; y < rows; ++y) {
                    int xCoord = 0;
                    for (int x = 0; x < cols - 1; ++x) {
                        chunkedImage.add(Bitmap.createBitmap(originalBitmap, xCoord, yCoord, chunkSideLength, chunkSideLength));
                        xCoord += chunkSideLength;
                    }
                    // add last chunk in a row to array of wider chunks
                    widerChunks.add(Bitmap.createBitmap(originalBitmap, xCoord, yCoord, widerChunkSide, chunkSideLength));

                    yCoord += chunkSideLength;
                }

                //add wider chunks into resulting array of chunks
                //Each wider chunk should be in his own row to preserve original image size
                for (int y = 0; y < rows; ++y) {
                    chunkedImage.add(cols * y + cols - 1, widerChunks.get(y));
                }

            } else {
                // picture perfectly splits into square chunks
                int yCoord = 0;
                for (int y = 0; y < rows; ++y) {
                    int xCoord = 0;
                    for (int x = 0; x < cols; ++x) {
                        chunkedImage.add(Bitmap.createBitmap(originalBitmap, xCoord, yCoord, chunkSideLength, chunkSideLength));
                        xCoord += chunkSideLength;
                    }
                    yCoord += chunkSideLength;
                }

            }
        }

        return chunkedImage;
    }

    private List<TileListEntity> chopped(List<TileModel> list, final int cols) {
        List<TileListEntity> parts = new ArrayList<>();
        final int size = list.size();
        for (int i = 0; i < size; i += cols) {
            List<TileModel> tileModels = new ArrayList<>(list.subList(i, Math.min(size, i + cols)));
            parts.add(new TileListEntity(tileModels));
        }
        return parts;
    }

    private String getAverage(Bitmap bitmap) {
        if (null == bitmap) return "#00ffffff";

        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;

        int pixelCount = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[pixelCount];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int y = 0, h = bitmap.getHeight(); y < h; y++) {
            for (int x = 0, w = bitmap.getWidth(); x < w; x++) {
                int color = pixels[x + y * w]; // x + y * width
                redBucket += (color >> 16) & 0xFF; // Color.red
                greenBucket += (color >> 8) & 0xFF; // Color.greed
                blueBucket += (color & 0xFF); // Color.blue
            }
        }
        int intColor = Color.rgb(redBucket / pixelCount, greenBucket / pixelCount, blueBucket / pixelCount);
        return String.format("%06X", 0xFFFFFF & intColor);
    }
}
