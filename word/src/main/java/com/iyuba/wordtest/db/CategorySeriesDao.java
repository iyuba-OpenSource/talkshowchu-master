package com.iyuba.wordtest.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.iyuba.wordtest.entity.CategorySeries;

import java.util.List;

@Dao
public interface CategorySeriesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void saveCategory(CategorySeries... bookLevels);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCategory(CategorySeries... bookLevels);

    @Query("delete from CategorySeries where uid = :uid")
    void deleteCategory(int uid);

    @Delete
    void deleteCategory(CategorySeries book);

    @Query("select * from CategorySeries order by Category asc")
    List<CategorySeries> getAllCategory();

    @Query("select * from CategorySeries where uid = :uid order by Category asc")
    List<CategorySeries> getUidCategories(int uid);

    @Query("select * from CategorySeries where uid = :uid and isVideo = :isVideo order by Category asc")
    List<CategorySeries> getVideoCategories(int uid, String isVideo);

    @Query("select * from CategorySeries where Category = :Category and uid = :uid ")
    CategorySeries getUidCategory(int Category, int uid);

}
