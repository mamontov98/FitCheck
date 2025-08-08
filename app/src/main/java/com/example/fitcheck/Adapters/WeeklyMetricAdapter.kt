package com.example.fitcheck.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitcheck.R
import com.example.fitcheck.model.WeeklyMetric
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView

class WeeklyMetricAdapter(
    private val metrics: List<WeeklyMetric>
) : RecyclerView.Adapter<WeeklyMetricAdapter.MetricViewHolder>() {

    //ViewHolder for a single metric item
    class MetricViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val metricName: TextView = view.findViewById(R.id.tvMetricName)
        val graphView: SparkView = view.findViewById(R.id.metricGraph)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetricViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_metric, parent, false)
        return MetricViewHolder(view)
    }

    // Binds a WeeklyMetric to the ViewHolder, updating the name and graph
    override fun onBindViewHolder(holder: MetricViewHolder, position: Int) {
        val metric = metrics[position]
        bindMetric(holder, metric)
    }

    // function to bind a metric's data to its ViewHolder
    private fun bindMetric(holder: MetricViewHolder, metric: WeeklyMetric) {
        holder.metricName.text = metric.name
        holder.graphView.adapter = object : SparkAdapter() {
            override fun getCount(): Int = metric.values.size
            override fun getItem(index: Int): Any = metric.values[index]
            override fun getY(index: Int): Float = metric.values[index]
        }
    }

    // Returns the number of metrics in the list
    override fun getItemCount() = metrics.size
}
