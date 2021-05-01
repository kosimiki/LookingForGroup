package hu.blog.megosztanam;

import android.widget.AdapterView;
import android.widget.Spinner;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.sub.menu.NoticeBoardFragment;
import hu.blog.megosztanam.sub.menu.post.PostFilter;

import static org.mockito.Mockito.when;


public class NoticeBoardFragmentTest {

    NoticeBoardFragment noticeBoardFragment;

    @Before
    public void setup() {
        noticeBoardFragment = new NoticeBoardFragment();
        noticeBoardFragment.initializeMapNames("all", "sum", "tw", "how");
    }

    @Test
    public void createMapSelectionListenerTest() {
        PostFilter postFilter = new PostFilter();
        Spinner spinner = Mockito.mock(Spinner.class);
        when(spinner.getItemAtPosition(0)).thenReturn("all");
        when(spinner.getItemAtPosition(1)).thenReturn("sum");
        when(spinner.getItemAtPosition(2)).thenReturn("tw");
        when(spinner.getItemAtPosition(3)).thenReturn("how");
        final AtomicReference<String> methodCallResult = new AtomicReference<>();
        AdapterView.OnItemSelectedListener listener = noticeBoardFragment.createMapSelectionListener(postFilter, new Consumer<String>() {
            @Override
            public void accept(String s) {
                methodCallResult.set(s);
            }
        });
        listener.onItemSelected(spinner, null, 0, 0);
        assert postFilter.showAllMaps;
        assert methodCallResult.get().equals("show all maps");
        listener.onItemSelected(spinner, null, 1, 0);
        assert !postFilter.showAllMaps;
        assert postFilter.map == GameMap.SUMMONERS_RIFT;
        assert methodCallResult.get().equals("map selected");
        listener.onItemSelected(spinner, null, 2, 0);
        assert !postFilter.showAllMaps;
        assert postFilter.map == GameMap.TWISTED_TREE_LINE;
        assert methodCallResult.get().equals("map selected");
        listener.onItemSelected(spinner, null, 3, 0);
        assert !postFilter.showAllMaps;
        assert postFilter.map == GameMap.HOWLING_FJORD;
        assert methodCallResult.get().equals("map selected");
        listener.onNothingSelected(spinner);
        assert postFilter.showAllMaps;
        assert methodCallResult.get().equals("show all maps");

    }
}