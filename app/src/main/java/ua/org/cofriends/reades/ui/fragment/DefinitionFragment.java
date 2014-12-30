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
import org.dict.kernel.IWordPosition;

import java.util.ArrayList;
import java.util.List;

import ua.org.cofriends.reades.R;
import ua.org.cofriends.reades.entity.Word;
import ua.org.cofriends.reades.service.SavedWordsService;
import ua.org.cofriends.reades.ui.tools.BaseToast;
import ua.org.cofriends.reades.utils.BundleUtils;
import ua.org.cofriends.reades.utils.BusUtils;

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

        IWordPosition[] wordPositions = answer.getAdjacentWords().getWordPositions();

        CharSequence[] words = new CharSequence[wordPositions.length];
        int count = 0;
        for (IWordPosition position : wordPositions) {
            words[count++] = position.getKey();
        }
        args.putCharSequenceArray(ARG_ADJACENT, words);
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
        // start building dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (word != null) {
            wordName = word.getWord();

            builder.setNegativeButton(android.R.string.ok, null)
                    .setMessage(word.getDefinition());
        } else {
            wordName = BundleUtils.getString(getArguments(), ARG_WORD);
            String wordDefinition = BundleUtils.getString(getArguments(), ARG_DEFINITION);

            if (wordDefinition == null) {
                BaseToast.show(getActivity(), R.string.message_no_definition);

                final CharSequence[] adjacentWords = getArguments().getCharSequenceArray(ARG_ADJACENT);

                builder.setItems(adjacentWords, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BusUtils.post(new PageFragment.WordRequestEvent(adjacentWords[which]));
                        dismiss();
                    }
                });
            } else {
                builder.setMessage(wordDefinition)
                        .setPositiveButton(R.string.title_add_to_my_words, this);
            }

            builder.setNegativeButton(android.R.string.cancel, null);
        }

        builder.setTitle(wordName);

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
