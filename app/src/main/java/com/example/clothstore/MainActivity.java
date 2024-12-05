package com.example.clothstore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private EditText nameEd, descEd;
    private Button addBtn, updateBtn, deleteBtn, selectImgBtn;
    private ListView listView;
    private ImageView imageView;
    private String selectedItemName;
    private String selectedImagePath;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        nameEd = findViewById(R.id.nameText);
        descEd = findViewById(R.id.descText);
        addBtn = findViewById(R.id.addButton);
        updateBtn = findViewById(R.id.updateButton);
        deleteBtn = findViewById(R.id.deleteButton);
        selectImgBtn = findViewById(R.id.selectImgButton);
        listView = findViewById(R.id.listView);
        imageView = findViewById(R.id.imageView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getItemNames());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedItemName = adapter.getItem(position);
            Cloth item = Paper.book().read(selectedItemName, null);
            if (item != null) {
                nameEd.setText(item.getName());
                descEd.setText(item.getDescription());
                selectedImagePath = item.getImagePath();
                loadImage(selectedImagePath);
            }
        });

        addBtn.setOnClickListener(v -> {
            String name = nameEd.getText().toString();
            String desc = descEd.getText().toString();

            if (!name.isEmpty() && !desc.isEmpty()) {
                if (selectedImagePath == null) {
                    Toast.makeText(MainActivity.this, "Пожалуйста, выберите изображение товара", Toast.LENGTH_SHORT).show();
                } else {
                    Cloth item = new Cloth(name, desc, selectedImagePath);
                    Paper.book().write(name, item);
                    updateItemList();
                    clearInputs();
                }
            } else {
                Toast.makeText(MainActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            }
        });


        updateBtn.setOnClickListener(v -> {
            if (selectedItemName == null) {
                Toast.makeText(this, "Пожалуйста, выберите товар", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = nameEd.getText().toString();
            String desc = descEd.getText().toString();

            if (!name.isEmpty() && !desc.isEmpty() && selectedImagePath != null) {
                Cloth item = new Cloth(name, desc, selectedImagePath);
                Paper.book().write(selectedItemName, item);
                updateItemList();
                clearInputs();
            }
        });

        deleteBtn.setOnClickListener(v -> {
            if (selectedItemName == null) {
                Toast.makeText(this, "Пожалуйста, выберите товар", Toast.LENGTH_SHORT).show();
                return;
            }

            Paper.book().delete(selectedItemName);
            updateItemList();
            clearInputs();
        });

        selectImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });
    }

    private void clearInputs() {
        nameEd.setText("");
        descEd.setText("");
        imageView.setImageResource(R.drawable.image);
        selectedItemName = null;
        selectedImagePath = null;
    }

    private void updateItemList() {
        adapter.clear();
        adapter.addAll(getItemNames());
        adapter.notifyDataSetChanged();
    }

    private List<String> getItemNames() {
        return new ArrayList<>(Paper.book().getAllKeys());
    }

    private void loadImage(String imagePath) {
        if (imagePath != null) {
            Uri imageUri = Uri.parse(imagePath);
            imageView.setImageURI(imageUri);
        } else {
            imageView.setImageResource(R.drawable.image);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            selectedImagePath = selectedImageUri.toString();
            imageView.setImageURI(selectedImageUri);
        }
    }
}