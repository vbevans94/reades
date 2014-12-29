package ua.org.cofriends.reades.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.ArrayAdapter;

import org.dict.kernel.IAnswer;

import java.util.List;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Word;
import ua.org.cofriends.reades.service.SavedWordsService;
import ua.org.cofriends.reades.utils.BundleUtils;

public class DefinitionFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String TAG_DEFINITION_FRAGMENT = "tag_definition_fragment";
    private static final String ARG_DEFINITION = "arg_definition";
    private static final String ARG_WORD = "arg_word";
    private static final String ARG_ADJACENT = "arg_adjacent";

    public static void show(FragmentManager fragmentManager, IAnswer answer) {
        DefinitionFragment fragment = new DefinitionFragment();
        Bundle args = BundleUtils.putString(
                BundleUtils.putString(null, ARG_WORD, answer.getKey())
                , ARG_DEFINITION, answer.getDefinition());
        // args.putStringArrayList(ARG_ADJACENT, answer.getAdjacentWords().);
        fragment.setArguments(args);
        fragment.show(fragmentManager, TAG_DEFINITION_FRAGMENT);
    }

    public static void show(FragmentManager fragmentManager, Word word) {
        DefinitionFragment fragment = new DefinitionFragment();
        fragment.setArguments(BundleUtils.writeObject(Word.class, word));
        fragment.show(fragmentManager, TAG_DEFINITION_FRAGMENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // see if it's just learned word or newly translated
        Word word = BundleUtils.fetchFromBundle(Word.class, getArguments());
        String wordName;
        String wordDefinition;
        // start building dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (word != null) {
            wordName = word.getWord();
            wordDefinition = word.getDefinition();

            builder.setNegativeButton(android.R.string.ok, null);
        } else {
            wordName = BundleUtils.getString(getArguments(), ARG_WORD);
            wordDefinition = BundleUtils.getString(getArguments(), ARG_DEFINITION);
            List<String> adjacentWords = getArguments().getStringArrayList(ARG_ADJACENT);

            builder.setPositiveButton(R.string.title_add_to_my_words, this)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, adjacentWords),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: go to adjacent word by its position
                                }
                            });
        }

        builder.setTitle(wordName)
                .setMessage(wordDefinition);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            Word word = new Word(BundleUtils.getString(getArguments(), ARG_WORD)
                    , BundleUtils.getString(getArguments(), ARG_DEFINITION)
                    , getArguments().getStringArrayList(ARG_ADJACENT));
            SavedWordsService.save(getActivity(), word);
        }
    }
}
