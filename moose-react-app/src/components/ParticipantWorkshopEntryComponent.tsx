import {Button, ListGroup} from "react-bootstrap";
import {RegistrationStatus, WorkshopEntryRegistrationType} from "../ServerTypes";

interface ParticipantWorkshopEntryComponentProps {
    workshopEntryRegistrationType:WorkshopEntryRegistrationType
    onSetCheckin: (registrationId:string,checkinFlag:boolean) => void
}

const ParticipantWorkshopEntryComponent:React.FC<ParticipantWorkshopEntryComponentProps> = ({workshopEntryRegistrationType,onSetCheckin}:ParticipantWorkshopEntryComponentProps) => {
    const itemVariant = (workshopEntryRegistrationType.isCheckedIn) ? "success" : (workshopEntryRegistrationType.registrationStatus === RegistrationStatus.WAITING) ? "warning" : "light"
    return (<ListGroup.Item variant={itemVariant}>
        {workshopEntryRegistrationType.registrationNumber}.
        <a href={"/participant/" + workshopEntryRegistrationType.partcipantId}>{workshopEntryRegistrationType.participantName} ({workshopEntryRegistrationType.participantEmail})</a>
        &nbsp;
        {workshopEntryRegistrationType.registrationStatus}.
        &nbsp;
        {workshopEntryRegistrationType.isCheckedIn && <span>Checked in&nbsp;<Button variant={"danger"} onClick={() => onSetCheckin(workshopEntryRegistrationType.registrationId,false)}>Cancel checkin</Button></span>}
        {!workshopEntryRegistrationType.isCheckedIn && <span>Not checked in&nbsp;<Button onClick={() => onSetCheckin(workshopEntryRegistrationType.registrationId,true)}>Do checkin</Button></span>}

    </ListGroup.Item>);
};

export default ParticipantWorkshopEntryComponent;