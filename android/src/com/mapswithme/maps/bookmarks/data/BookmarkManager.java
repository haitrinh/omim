package com.mapswithme.maps.bookmarks.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;

import com.mapswithme.maps.MWMActivity;
import com.mapswithme.maps.R;
import com.mapswithme.util.Utils;

public class BookmarkManager
{
  private static BookmarkManager sManager;
  private List<Bookmark> mPins;
  private List<BookmarkCategory> mPinSets;
  private Context mContext;
  private BookmarkIconManager mIconManager;

  private BookmarkManager(Context context)
  {
    mContext = context;
    refreshList();
    mIconManager = new BookmarkIconManager(context);
  }

  public static BookmarkManager getBookmarkManager(Context context)
  {
    if (sManager == null)
    {
      sManager = new BookmarkManager(context.getApplicationContext());
    }

    return sManager;
  }

  private void refreshList()
  {
    nLoadBookmarks();
  }

  private native void nLoadBookmarks();

  public void deleteBookmark(Bookmark bmk)
  {
    nDeleteBookmark(bmk.getCategoryId(), bmk.getBookmarkId());
  }

  public void deleteBookmark(int cat, int bmk)
  {
    nDeleteBookmark(cat, bmk);
  }

  private native void nDeleteBookmark(int c, int b);

  public BookmarkCategory getCategoryById(int id)
  {
    if (id < getCategoriesCount())
    {
      return new BookmarkCategory(mContext, id);
    }
    else
    {
      return null;
    }
  }

  public native int getCategoriesCount();

  public void deleteCategory(int index)
  {
    nDeleteCategory(index);
  }

  private native boolean nDeleteCategory(int index);

  Icon getIconByName(String name)
  {
    return mIconManager.getIcon(name);
  }

  public List<Icon> getIcons()
  {
    return new ArrayList<Icon>(mIconManager.getAll().values());
  }

  public Bookmark getBookmark(ParcelablePointD p)
  {
    Point bookmark = nGetBookmark(p.x, p.y);
    if (bookmark.x == -1 && bookmark.y == -1)
    {
      return new Bookmark(mContext, p, getCategoriesCount() - 1, getCategoriesCount() - 1 >= 0 ? getCategoryById(getCategoriesCount() - 1).getSize() : 0);
    }
    else
    {
      return new Bookmark(mContext, new BookmarkCategory(mContext, bookmark.x).getId(), bookmark.y);
    }
  }

  public ParcelablePoint findBookmark(ParcelablePointD p)
  {
    Point bookmark = nGetBookmark(p.x, p.y);
    if (bookmark.x>=0 && bookmark.y>=0)
    {
      return new ParcelablePoint(bookmark);
    }
    else
      return null;
  }

  static native Point nGetBookmark(double px, double py);

  public Bookmark getBookmark(int cat, int bmk)
  {

    return new Bookmark(mContext, cat, bmk);
  }

  public BookmarkCategory createCategory(Bookmark bookmark, String newName)
  {
    String pattern;
    String name = pattern = newName;
    int i = 0;
    while (getCategoryByName(name))
    {
      name = pattern + " " + (++i);
    }
    bookmark.setCategory(name, getCategoriesCount());
    BookmarkCategory cat = new BookmarkCategory(mContext, getCategoriesCount()-1);
    return cat;
  }

  //TODO
  public boolean getCategoryByName(String name)
  {
    return nGetCategoryByName(name);
  }

  private native boolean nGetCategoryByName(String name);

  public Bookmark previewBookmark(AddressInfo info)
  {
    return new Bookmark(mContext, info.getPosition(), info.getBookmarkName(mContext));
  }

  private native void nShowBookmark(int c, int b);

  public void showBookmarkOnMap(int c, int b)
  {
    nShowBookmark(c, b);
  }

  private native String nGetNameForPlace(double px, double py);

  public String getNameForPlace(ParcelablePointD p)
  {
    return Utils.toTitleCase(nGetNameForPlace(p.x,p.y));
  }

  public AddressInfo getPOI(ParcelablePointD px)
  {
    return nGetPOI(px.x, px.y);
  }
  private native AddressInfo nGetPOI(double px, double py);

  public AddressInfo getAddressInfo(ParcelablePointD px)
  {
    return nGetAddressInfo(px.x, px.y);
  }
  private native AddressInfo nGetAddressInfo(double px, double py);
}
