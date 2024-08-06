import {AdminWorkshopType} from "../ServerTypes";
import AdminWorkshopRegistrationComponent from "./AdminWorkshopRegistrationComponent";

interface AdminWorksopProps {
    workshop:AdminWorkshopType
}

const AdminWorkshopComponent = ({workshop}:AdminWorksopProps) => {
    return (<div>
        <h2>{workshop.name}</h2>
        <p>Type: {workshop.workshopType}</p>
        <p>Status: {workshop.workshopstatus}</p>
        <p>Opens At: {workshop.opensAt}</p>
        <p>Register Limit: {workshop.registerLimit}</p>
        <p>Capacity: {workshop.capacity}</p>
        <p>Seats Taken: {workshop.seatsTaken}</p>
        <h3>Participants</h3>
        {workshop.registrationList.map((registration,partIndex) =>
            <AdminWorkshopRegistrationComponent registration={registration} key={registration.id} participantNumber={partIndex+1}/>
        )}
        {workshop.registrationList.length === 0 && <div>No registrations yet</div>}
    </div>);
};

export default AdminWorkshopComponent;