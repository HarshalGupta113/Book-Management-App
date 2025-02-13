package com.example.bookmanagement.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmanagement.R;
import com.example.bookmanagement.model.Book;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> bookList;
    private Context context;
    private Runnable updateEmptyView;

    public BookAdapter(List<Book> bookList, Context context, Runnable updateEmptyView) {
        this.bookList = bookList;
        this.context = context;
        this.updateEmptyView = updateEmptyView;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookName.setText(book.getName());
        holder.quantity.setText("Quantity: " + book.getQuantity());

        holder.sellButton.setOnClickListener(v -> showSellDialog(holder, position));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    private void showSellDialog(BookViewHolder holder, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sell Book");
        builder.setMessage("Book Name: "+bookList.get(position).getName());

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_sell_book, null);
        TextInputEditText quantityInput = view.findViewById(R.id.edit_quantity);

        builder.setView(view);
        builder.setPositiveButton("Sell", (dialog, which) -> {
            String qtyText = quantityInput.getText().toString().trim();
            if (!qtyText.isEmpty()) {
                try {
                    int sellQty = Integer.parseInt(qtyText);
                    Book book = bookList.get(position);

                    if (sellQty <= 0) {
                        Snackbar.make(holder.itemView, "Enter a valid quantity!", Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(context.getColor(R.color.red))
                                .show();
                        return;
                    }

                    if (book.getQuantity() >= sellQty) {
                        book.setQuantity(book.getQuantity() - sellQty);

                        if (book.getQuantity() == 0) {
                            bookList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, bookList.size()); // Update positions
                            Snackbar.make(holder.itemView, "Book sold out and removed!", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(context.getColor(R.color.green))
                                    .show();
                        } else {
                            notifyItemChanged(position);
                            Snackbar.make(holder.itemView, "Book sold!", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(context.getColor(R.color.green))
                                    .show();
                        }

                    } else {
                        Snackbar.make(holder.itemView, "Not enough stock!", Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(context.getColor(R.color.red))
                                .show();
                    }
                } catch (NumberFormatException e) {
                    Snackbar.make(holder.itemView, "Invalid quantity!", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(context.getColor(R.color.red))
                            .show();
                }
            } else {
                Snackbar.make(holder.itemView, "Enter quantity!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(context.getColor(R.color.red))
                        .show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookName, quantity;
        Button sellButton;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.text_book_name);
            quantity = itemView.findViewById(R.id.text_quantity);
            sellButton = itemView.findViewById(R.id.button_sell);
        }
    }
}
