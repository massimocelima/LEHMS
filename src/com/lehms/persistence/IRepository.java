package com.lehms.persistence;

import android.database.SQLException;

public interface IRepository {
    public void open() throws SQLException;
    public void close();
}
