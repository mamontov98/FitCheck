import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.fitcheck.R
import com.example.fitcheck.model.WorkoutEntry

class ExerciseAdapter(
    private val exercises: MutableList<WorkoutEntry>
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // ViewHolder for a single exercise row
        val etName: EditText = view.findViewById(R.id.etExerciseName)
        val etSets: EditText = view.findViewById(R.id.etSets)
        val etReps: EditText = view.findViewById(R.id.etReps)
        val etWeight: EditText = view.findViewById(R.id.etWeight)
        val etPrevReps: EditText = view.findViewById(R.id.etPrevReps)
        val etPrevWeight: EditText = view.findViewById(R.id.etPrevWeight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    //Binds the WorkoutEntry data to the views, and attaches listeners for changes
    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]

        // Set editable fields
        if (holder.etName.text.toString() != exercise.exerciseName)
            holder.etName.setText(exercise.exerciseName)
        if (holder.etSets.text.toString() != exercise.sets.toString())
            holder.etSets.setText(exercise.sets.toString())
        if (holder.etReps.text.toString() != exercise.reps)
            holder.etReps.setText(exercise.reps)
        if (holder.etWeight.text.toString() != exercise.weights)
            holder.etWeight.setText(exercise.weights)

        // Set previous values
        holder.etPrevReps.setText(exercise.prevReps)
        holder.etPrevWeight.setText(exercise.prevWeight)

        // מאזינים לעדכון הנתונים (רק לשדות הניתנים לעריכה)
        holder.etName.doAfterTextChanged { exercise.exerciseName = it?.toString() ?: "" }
        holder.etSets.doAfterTextChanged { exercise.sets = it?.toString()?.toIntOrNull() ?: 0 }
        holder.etReps.doAfterTextChanged { exercise.reps = it?.toString() ?: "" }
        holder.etWeight.doAfterTextChanged { exercise.weights = it?.toString() ?: "" }
    }

    //Returns the number of exercises in the list
    override fun getItemCount() = exercises.size
}
