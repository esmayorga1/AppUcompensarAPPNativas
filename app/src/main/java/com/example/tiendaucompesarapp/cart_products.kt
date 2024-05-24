package com.example.tiendaucompesarapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream
import java.net.HttpURLConnection
import java.net.URL

class cart_products : AppCompatActivity() {
    var cantidad = 1
    lateinit var cantidadTextView: TextView

    private var totalPrice = 0.0
    private var price = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart_products)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var bundle = intent.extras
        val productosList: List<Producto>? = bundle?.get("productos") as? List<Producto>

        if (productosList.isNullOrEmpty()) {
            Toast.makeText(this, "La lista de productos está vacía", Toast.LENGTH_SHORT).show()
        } else {
            // Agregar cada producto al contenedor
            val productoContainer = findViewById<LinearLayout>(R.id.productoContainer2)
            val priceTextView = findViewById<TextView>(R.id.price)
            for (producto in productosList) {
                agregarProductoAlContenedor(producto, productoContainer, priceTextView)
            }
        }

        val btArrow = findViewById<ImageView>(R.id.BotonAtras)
        btArrow.setOnClickListener {
            finish()
        }
    }

    private fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
        val thread = Thread {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)

                runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun agregarProductoAlContenedor(
        producto: Producto,
        contenedor: LinearLayout,
        priceTextView: TextView
    ) {
        val inflater = layoutInflater
        val productoView = inflater.inflate(R.layout.activity_cart_item, contenedor, false)

        val nombreTextView = productoView.findViewById<TextView>(R.id.textView7)
        val precioTextView = productoView.findViewById<TextView>(R.id.textView8)
        val imageView = productoView.findViewById<ImageView>(R.id.imageView23)
        val cantidadTextView = productoView.findViewById<TextView>(R.id.textViewCantidad)
        val addImageView = productoView.findViewById<ImageView>(R.id.mas)
        val resImageView = productoView.findViewById<ImageView>(R.id.menos)

        var cantidad = 1
        nombreTextView.text = producto.nombre
        precioTextView.text = (producto.precio * cantidad).toString()
        loadImageFromUrl(imageView, producto.imagenUrl)
        totalPrice += producto.precio // Incrementar el precio total
        priceTextView.text = totalPrice.toString() // Actualizar el TextView del precio total

        addImageView.setOnClickListener {
            cantidad++
            cantidadTextView.text = cantidad.toString()
            precioTextView.text = (producto.precio * cantidad).toString()
            totalPrice += producto.precio
            priceTextView.text = totalPrice.toString()

        }

        resImageView.setOnClickListener {
            if (cantidad > 0) {
                cantidad--
                cantidadTextView.text = cantidad.toString()
                precioTextView.text = (producto.precio * cantidad).toString()
                totalPrice -= producto.precio
                priceTextView.text = totalPrice.toString()
            }
            if (cantidad == 0) {
                contenedor.removeView(productoView)
            }
        }
        contenedor.addView(productoView)
    }
}