import {AdminWorkshopRegistrationType} from "../ServerTypes";

interface AdminWorkshopRegistrationComponentProps {
    registration: AdminWorkshopRegistrationType,
    participantNumber:number
}

const AdminWorkshopRegistrationComponent = ({registration,participantNumber}:AdminWorkshopRegistrationComponentProps) => {
    return (<div>
        <h4>{participantNumber}. {registration.name}</h4>
        <p>Status: {registration.status}</p>
        <p>Email: {registration.email}</p>
        <p>Spots reserved: {registration.numSpots}</p>
        <p>Registered at: {registration.registeredAt}</p>
        <p><a href={"/registration/" + registration.id}>Cancel registration</a> <a href={"/participant/" + registration.participantId}>See participants registations</a> </p>
    </div>);
}

export default AdminWorkshopRegistrationComponent