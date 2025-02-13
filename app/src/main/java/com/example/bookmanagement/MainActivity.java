package com.example.bookmanagement;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanagement.adapter.BookAdapter;
import com.example.bookmanagement.model.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;
    private FloatingActionButton fabAddBook;
    private TextView textNoBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recycler_view);
        fabAddBook = findViewById(R.id.fab_add_book);
        textNoBooks = findViewById(R.id.text_no_books);

        bookList = new ArrayList<>();
        adapter = new BookAdapter(bookList, this, this::updateEmptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateEmptyView();

        fabAddBook.setOnClickListener(v -> showAddBookDialog());

    }
    private void showAddBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Book");

        // Inflate the correct layout for adding a book
        View view = getLayoutInflater().inflate(R.layout.dialog_add_book, null);
        TextInputEditText bookNameInput = view.findViewById(R.id.edit_book_name);
        TextInputEditText quantityInput = view.findViewById(R.id.edit_quantity);

        builder.setView(view);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = bookNameInput.getText().toString().trim();
            String qtyText = quantityInput.getText().toString().trim();

            if (!name.isEmpty() && !qtyText.isEmpty()) {
                int quantity = Integer.parseInt(qtyText);
                bookList.add(new Book(name, quantity));
                adapter.notifyDataSetChanged();
                updateEmptyView(); // Hide "No book to display" when book is added

                Snackbar.make(recyclerView, "Book added!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getColor(R.color.green)).show();
            } else {
                Snackbar.make(recyclerView, "Invalid input!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getColor(R.color.red))
                        .show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    // Function to show/hide "No book to display"
    private void updateEmptyView() {
        if (bookList.isEmpty()) {
            textNoBooks.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textNoBooks.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}