package com.broadway.downfile.downfiledemo.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.broadway.downfile.downfiledemo.R;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    private DbManager db;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        db = XUtilsManager.getInstance(this).getDb();

        tv = (TextView) findViewById(R.id.text);
        Button add = (Button) findViewById(R.id.add);
        Button find = (Button) findViewById(R.id.find);
        final Button delete = (Button) findViewById(R.id.delete);
        Button delete_db = (Button) findViewById(R.id.delete_db);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dog dog = new Dog();
                dog.setId(1);
                dog.setAge(22);
                dog.setName("小黑");
                dog.setColor("黑色");
                dog.setTemap("temp");

                try {
                    db.save(dog);
                } catch (DbException e) {
                    e.printStackTrace();
                }

            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Dog> dogs = null;
                try {
                    dogs = db.findAll(Dog.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                String text = "";
                if (dogs != null && dogs.size() != 0) {
                    for (int i = 0; i < dogs.size(); i++) {
                        Dog dog = dogs.get(i);
                        text += "\n"+dog.getId() + "-" + dog.getName() + "=" + dog.getAge() + "=" + dog.getColor()+"="+dog.getTemap();
                    }
                }
                if (!"".equals(text)) {
                    tv.setText(text);
                }else{
                    tv.setText("空");
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                   db.delete(Dog.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

        delete_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    db.dropDb();
                } catch (DbException e) {
                    e.printStackTrace();
                }

            }
        });
    }


}
