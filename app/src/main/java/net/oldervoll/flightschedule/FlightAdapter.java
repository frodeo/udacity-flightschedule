package net.oldervoll.flightschedule;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Optional;

import net.oldervoll.flightschedule.data.FlightContract;

public class FlightAdapter extends CursorAdapter {

    public static final String LOG_TAG = FlightAdapter.class.getSimpleName();

    private final int VIEW_TYPE_FLIGHT_HIGHLIGHTED = 0;
    private final int VIEW_TYPE_FLIGHT = 1;
    @SuppressWarnings("unused")
    private boolean useTwoPaneLayout;

    public FlightAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = viewType == VIEW_TYPE_FLIGHT_HIGHLIGHTED ?
            R.layout.list_item_flight_highlighted : R.layout.list_item_flight;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int viewType = getItemViewType(cursor.getPosition());

        String flightId = cursor.getString(FlightFragment.COL_FLIGHT_ID);
        String arrDep = cursor.getString(FlightFragment.COL_ARR_DEP);
        String airportCode = cursor.getString(FlightFragment.COL_AIRPORT_CODE);
        String airportName = Optional.fromNullable(
            cursor.getString(FlightFragment.COL_AIRPORT_NAME)).or(airportCode);
        long scheduleTime = cursor.getLong(FlightFragment.COL_SCHEDULE_TIME);
        Optional<String> status = Utility.getStatus(context,
            cursor.getString(FlightFragment.COL_STATUS_CODE),
            cursor.getLong(FlightFragment.COL_STATUS_TIME));

        viewHolder.airportView.setText(airportName);
        viewHolder.flightIdView.setText(flightId);
        viewHolder.timeView.setText(Utility.formatDateTime(context, scheduleTime));
        if (status.isPresent()) {
            viewHolder.statusView.setText(status.get());
        }
        int arrDepImageResource = viewType == VIEW_TYPE_FLIGHT_HIGHLIGHTED ?
            Utility.getArtResourceForFlightArrDep(arrDep) :
            Utility.getIconResourceForFlightArrDep(arrDep);
        int arrDepImageContentDesc = FlightContract.FlightEntry.ARRDEP_A.equalsIgnoreCase(arrDep) ?
                R.string.arr_dep_A : R.string.arr_dep_D;
        if (arrDepImageResource != -1 && viewHolder.iconView != null) {
            viewHolder.iconView.setImageResource(arrDepImageResource);
            viewHolder.iconView.setContentDescription(context.getString(arrDepImageContentDesc));
        }
        if (viewHolder.airlineView != null) {
            String airlineCode = cursor.getString(FlightFragment.COL_AIRLINE_CODE);
            String airlineName = Optional.fromNullable(
                cursor.getString(FlightFragment.COL_AIRLINE_NAME)).or(airlineCode);
            viewHolder.airlineView.setText(airlineName);
        }
        if (viewHolder.gateView != null) {
            String gate = cursor.getString(FlightFragment.COL_GATE);
            viewHolder.gateView.setText(gate == null ? "" :
                context.getString(R.string.format_gate, gate));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_FLIGHT_HIGHLIGHTED : VIEW_TYPE_FLIGHT;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void setUseTwoPaneLayout(boolean useTwoPaneLayout) {
        this.useTwoPaneLayout = useTwoPaneLayout;
    }
    
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView airportView;
        public final TextView flightIdView;
        public final TextView timeView;
        public final TextView statusView;
        public final TextView airlineView;
        public final TextView gateView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            airportView = (TextView) view.findViewById(R.id.list_item_airport_textview);
            flightIdView = (TextView) view.findViewById(R.id.list_item_flight_id_textview);
            timeView = (TextView) view.findViewById(R.id.list_item_time_textview);
            statusView = (TextView) view.findViewById(R.id.list_item_status_textview);
            airlineView = (TextView) view.findViewById(R.id.list_item_airline_textview);
            gateView = (TextView) view.findViewById(R.id.list_item_gate_textview);
        }
    }
}