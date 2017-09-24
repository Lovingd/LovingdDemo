package com.muzi.lovingd.dao;

/**
 * EntityManager
 *
 * @author: 17040880
 * @time: 2017/9/18 20:19
 */
public class EntityManager {
    private static EntityManager entityManager;
    public SaveCalendarItemDao userDao;

    /**
     * 创建User表实例
     *
     * @return
     */
    public SaveCalendarItemDao getSaveCalendarItemDao(){
        userDao = DaoManager.getInstance().getSession().getSaveCalendarItemDao();
        return userDao;
    }

    /**
     * 创建单例
     *
     * @return
     */
    public static EntityManager getInstance() {
        if (entityManager == null) {
            entityManager = new EntityManager();
        }
        return entityManager;
    }
}