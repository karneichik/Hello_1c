package by.karneichik.hello1c

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import by.karneichik.hello1c.adapters.ProductListAdapter
import by.karneichik.hello1c.viewModels.OrderViewModel
import by.karneichik.hello1c.viewModels.ProductsViewModel
import kotlinx.android.synthetic.main.activity_order_detail.*


class OrderDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: OrderViewModel
    private lateinit var productsViewModel: ProductsViewModel
    private var adapter: ProductListAdapter = ProductListAdapter(this)
    private val p = Paint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        supportActionBar?.let { it.setDisplayHomeAsUpEnabled(true) }

        if (!intent.hasExtra(EXTRA_UID_ORDER)) {
            finish()
            return
        }
        val uid = intent.getStringExtra(EXTRA_UID_ORDER)!!

        viewModel = ViewModelProviders.of(this)[OrderViewModel::class.java]
        viewModel.getOrderInfo(uid).observe(this, Observer {
            with(it.order) {
                tvClient_FIO.text = client_fio
                tvClient_phone.text = client_phone.toString()
                tvAddress.text = address
                tvAddress.setOnClickListener {
                    intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("yandexnavi://map_search").buildUpon().appendQueryParameter("text", address).build()
                    try {
                        startActivity(intent)
                    } catch (e:Exception) {
                        Toast.makeText(this@OrderDetailActivity,"Яндекс Навигатор не установлен",Toast.LENGTH_LONG).show()
                    }

                }
                tvTotalSum.text = totalsum.toString()
                tvPayForm.text = payform
                tvTime.text = time
//                tvNumber.text = number
                supportActionBar?.title = number
            }
        })

//        val adapter = ProductListAdapter(this)
        rvProductList.adapter = adapter

        productsViewModel = ViewModelProviders.of(this)[ProductsViewModel::class.java]
        productsViewModel.getProductsList(uid).observe(this, Observer {
            adapter.productInfoList = it
        })
//        productsViewModel.getOrderInfo(uid).observe(this, Observer {
//            adapter.productInfoList = it.productsList
//        })

        enableSwipe()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun enableSwipe() {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val product = adapter.getProduct(position)

                    product.delivered = !product.delivered

                    AsyncTask.execute {
                        productsViewModel.update(product)
                    }
                    adapter.itemUpdate()


                }


                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    val icon: Bitmap
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                        val itemView = viewHolder.itemView
                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3
                        val myHolder : ProductListAdapter.ProductViewHolder = viewHolder as ProductListAdapter.ProductViewHolder
                        val isDelivered = myHolder.isDelivered

                        if (isDelivered) {
                            icon = getBitmapFromVectorDrawable( this@OrderDetailActivity, R.drawable.ic_clear_24px)
                            p.color = this@OrderDetailActivity.getColor(R.color.colorAccent)
                        }
                        else {
                            icon = getBitmapFromVectorDrawable(this@OrderDetailActivity, R.drawable.ic_add_24px)
                            p.color = this@OrderDetailActivity.getColor(R.color.colorPrimaryDark)
                        }

                        if (dX > 0 ) {
//                            p.color = this@OrderDetailActivity.getColor(R.color.colorAccent)
                            val background =
                                RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                            c.drawRect(background, p)
//                            icon = getBitmapFromVectorDrawable( this@OrderDetailActivity, R.drawable.ic_clear_24px)
                            val icon_dest = RectF(
                                itemView.left.toFloat() + width,
                                itemView.top.toFloat() + width,
                                itemView.left.toFloat() + 2 * width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)
                        } else {
//                            p.color = this@OrderDetailActivity.getColor(R.color.colorPrimaryDark)
                            val background = RectF(
                                itemView.right.toFloat() + dX,
                                itemView.top.toFloat(),
                                itemView.right.toFloat(),
                                itemView.bottom.toFloat()
                            )
                            c.drawRect(background, p)
//                            icon = getBitmapFromVectorDrawable(this@OrderDetailActivity, R.drawable.ic_add_24px)
                            val icon_dest = RectF(
                                itemView.right.toFloat() - 2 * width,
                                itemView.top.toFloat() + width,
                                itemView.right.toFloat() - width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)
                        }
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rvProductList)
    }

    fun getBitmapFromVectorDrawable(
        context: Context,
        drawableId: Int
    ): Bitmap {
        val drawable =
            ContextCompat.getDrawable(context, drawableId)

        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        with(drawable) {
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
        }
        return bitmap
    }

    companion object {
        private const val EXTRA_UID_ORDER = "uid"

        fun newIntent(context: Context, uid: String): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(EXTRA_UID_ORDER, uid)
            return intent
        }
    }
}
