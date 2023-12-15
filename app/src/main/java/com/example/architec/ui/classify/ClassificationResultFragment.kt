import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.architec.R
import com.example.architec.ui.classify.ClassifyFragment

class ClassificationResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_classification_result, container, false)

        // Retrieve data from ClassifyFragment
        val photoFilePath = (requireActivity() as ClassifyFragment).photoFilePath

        // Use the data as needed

        return view
    }
}
