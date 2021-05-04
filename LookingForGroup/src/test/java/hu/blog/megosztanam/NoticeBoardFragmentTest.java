package hu.blog.megosztanam;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import hu.blog.megosztanam.model.shared.GameMap;
import hu.blog.megosztanam.sub.menu.NoticeBoardFragment;
import hu.blog.megosztanam.sub.menu.post.PostFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


public class NoticeBoardFragmentTest {

    NoticeBoardFragment noticeBoardFragment;

    @Before
    public void setup() {
        noticeBoardFragment = new NoticeBoardFragment();
        noticeBoardFragment.initializeMapNames("all",
                "sum",
                "tw",
                "how");
    }

    @Test
    public void createMapSelectionListenerTest() {
        PostFilter postFilter = new PostFilter();
        Spinner spinner = Mockito.mock(Spinner.class);
        when(spinner.getItemAtPosition(0)).thenReturn("all");
        when(spinner.getItemAtPosition(1)).thenReturn("sum");
        final AtomicReference<String> methodCallResult = new AtomicReference<>();
        OnItemSelectedListener listener =
                noticeBoardFragment.createMapSelectionListener(postFilter, methodCallResult::set);

        listener.onItemSelected(spinner, null, 0, 0);
        assertTrue(postFilter.showAllMaps);
        assertEquals("show all maps", methodCallResult.get());

        listener.onItemSelected(spinner, null, 1, 0);
        assertFalse(postFilter.showAllMaps);
        assertEquals(GameMap.SUMMONERS_RIFT, postFilter.map);
        assertEquals("map selected", methodCallResult.get());

        listener.onNothingSelected(spinner);
        assertTrue(postFilter.showAllMaps);
        assertEquals("show all maps", methodCallResult.get());

    }
    
}