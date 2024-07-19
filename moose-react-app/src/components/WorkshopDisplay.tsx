import {WorkshopType} from "../ServerTypes";

interface WorkshopDisplayProps {
    workshop:WorkshopType,
    displayLink:boolean,
}

const WorkshopDisplay = (props: WorkshopDisplayProps) => {
    const { workshop, displayLink } = props;
    return (<div>
        <h2>{workshop.name}</h2>
        <p>Status: {workshop.workshopstatus}</p>
        {displayLink && <p><a href={"/workshop/" + workshop.id}>Register</a></p>}


    </div>);

};
export default WorkshopDisplay;