import {AddParticipantInput, WorkshopType} from "../ServerTypes";

interface WorkshopRegistrationProps {
    workshop:WorkshopType,
    accessToken:string | null,
    onRegisterParticipant:(addParticipantInput:AddParticipantInput)=>Promise<string|null>,

}

const WorkshopRegistration : React.FC<WorkshopRegistrationProps> = (props) => {
    return (<div></div>);
};

export default WorkshopRegistration;