package com.ev.spider;

import com.ev.spider.bean.GbEntity;
import com.ev.spider.dao.GbEntityRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Hawk
 * @company EV_GLOBE
 * @create 2020-09-04 14:33
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GbEntityRepositoryTest {

    @Autowired
    private GbEntityRepository gbEntityRepository;
    @Test
    public void addGbEntity() {
        GbEntity gbEntity = new GbEntity();
        gbEntity.setId(1);
        gbEntity.setName("test");
        gbEntity.setRegisterId("asdf");
        gbEntity.setVersion("fasdf");

        gbEntityRepository.save(gbEntity);
    }
}
