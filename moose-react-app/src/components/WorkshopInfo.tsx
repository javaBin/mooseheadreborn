import {AddParticipantInput, WorkshopType} from "../ServerTypes";
import RegisterParticipant from "./RegisterParticipant";
import WorkshopDisplay from "./WorkshopDisplay";

interface WorkshopInfoProps {
    workshop:WorkshopType
}
const WorkshopInfo: React.FC<WorkshopInfoProps> = ({workshop}) => {
    const handleRegisterParticipant:(addParticipantInput:AddParticipantInput)=>Promise<string|null> = (participant: AddParticipantInput)  => {
        return new Promise((resolve,reject) => {

        });
    };
    return (<div>
        <h1>Registration</h1>
        <WorkshopDisplay workshop={workshop} displayLink={false}/>

        <div>
            <RegisterParticipant onRegisterParticipant={handleRegisterParticipant}/>
        </div>
    </div>);
}

export default WorkshopInfo;