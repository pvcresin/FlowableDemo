package com.pvcresin.flowabledemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val compositeDisposable = CompositeDisposable()

    companion object {
        const val TAG = "BackPressure"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Observable

        val seekBar: SeekBar = findViewById(R.id.seekBar)
        val textView: TextView = findViewById(R.id.text)

        val subject = PublishSubject.create<Int>()

        val observableTask = subject.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(
                        { result ->
                            Log.d(TAG, "Observable " + result.toString())
                            textView.text = result.toString()
                        },
                        { t: Throwable? -> t?.printStackTrace() }
                )

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) {
                subject.onNext(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        compositeDisposable.add(observableTask)


        // Flowable

        val seekBar2: SeekBar = findViewById(R.id.seekBar2)
        val textView2: TextView = findViewById(R.id.text2)

        val processor = PublishProcessor.create<Int>()

        val flowableTask = processor
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(
                        { result ->
                            Log.d(TAG, "Flowable " + result.toString())
                            textView2.text = result.toString()
                        },
                        { t: Throwable? -> t?.printStackTrace() }
                )

        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) {
                processor.onNext(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        compositeDisposable.add(flowableTask)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
