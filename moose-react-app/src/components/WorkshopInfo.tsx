import {WorkshopType} from "../ServerTypes";

interface WorkshopInfoProps {
    workshop:WorkshopType
}
const WorkshopInfo: React.FC<WorkshopInfoProps> = ({workshop}) => {
    return (<div>
        <h2>{workshop.name}</h2>
        <p>Status: {workshop.workshopstatus}</p>
        <p><a href={"/workshop/" + workshop.id}>Register</a></p>
    </div>);
}

export default WorkshopInfo;